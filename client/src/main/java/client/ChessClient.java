package client;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.exceptions.*;
import model.EmptyRecord;
import model.requests.*;
import model.responses.*;
import ui.DisplayGame;
import websocket.commands.Connect;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.util.*;

import static client.EscapeSequences.*;


public class ChessClient {
    private final ServerFacade server;

    private State state = State.PRELOGIN;

    private String sessionAuth;
    private GameplayClient gameplayClient;

    private Map<String, Integer> gameMap = new HashMap<>();
    private final Gson SERIALIZER = new Gson();

    public ChessClient(String serverUrl) throws DataAccessException {
        server = new ServerFacade(serverUrl);
        gameplayClient = new GameplayClient(serverUrl);
    }

    public void preLoginRun(){
        System.out.println(LOGO + "Welcome to Chess, type 'help' to get started!");
        System.out.println(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        while(!result.equals("quit")){
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e){
                String msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    private String eval(String input){
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = tokens.length > 0 ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (state) {
                case State.PRELOGIN -> switch (cmd) {
                    case "register" -> register(params);
                    case "login" -> login(params);
                    case "quit" -> "quit";
                    default -> help();
                };
                case State.LOGGEDIN -> switch (cmd) {
                    case "logout" -> logout(params);
                    case "create" -> createGame(params);
                    case "list" -> listGames(params);
                    case "join" -> joinGame(params);
                    case "observe" -> observeGame(params);
                    default -> help();
                };
                case State.GAMEPLAY -> switch (cmd) {
                    case "leave" -> leaveGame();
                    case "move" ->gameplayClient.makeMove(params);
                    case "resign" -> gameplayClient.resign();
                    case "highlight" -> gameplayClient.highlight(params);
                    case "redraw" -> gameplayClient.redraw();
                    case "quit" -> "quit";
                    default -> help();
                };
            };
        } catch(DataAccessException e) {
            return e.getMessage();
        }
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + String.format("%s >>> ", state) + GREEN);
    }

    public String help(){
        return switch (state) {
            case State.PRELOGIN ->
                    """
                    register <USERNAME> <PASSWORD> <EMAIL>
                    login <USERNAME> <PASSWORD>
                    help - list commands
                    quit
                    """;
            case State.LOGGEDIN ->
                    """
                    create <NAME> - a game
                    list - games
                    join <ID> [WHITE|BLACK] - a game
                    observe <ID> - a game
                    logout - when you are done
                    quit - playing chess
                    help - with possible commands
                    """;
            case State.GAMEPLAY ->
                    """
                    redraw - the chessboard
                    move START POSITION to END POSITION - ex(move a2 to a4)
                    highlight [POSITION] - pieces moves ex(highlight [a2])
                    leave - game
                    quit - playing chess
                    help - with possible commands
                    """;
        };
    }

    private String register(String[] params){
        if (params.length < 3){
            params = Arrays.copyOf(params, 3);
        }
        RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
        try {
            RegisterResponse response = server.register(request);
            state = State.LOGGEDIN;
            sessionAuth = response.authToken();
            return "You have been successfully registered and logged in";
        } catch (DataAccessException e){
            return e.getMessage();
        }
    }

    private String login(String[] params){
        if (params.length < 2){
            params = Arrays.copyOf(params, 2);
        }
        LoginRequest request = new LoginRequest(params[0], params[1]);
        try{
            LoginResponse response = server.login(request);
            state = State.LOGGEDIN;
            sessionAuth = response.authToken();
            return "You have been successfully logged in";
        } catch (DataAccessException e){
            return e.getMessage();
        }
    }

    private String logout(String[] params){
        LogoutRequest request = new LogoutRequest(sessionAuth);
        try{
            server.logout(request);
            state = State.PRELOGIN;
            return "You have been successfully logged out";
        } catch (DataAccessException e) {
            return e.getMessage();
        }
    }

    private String createGame(String[] params){
        if (params.length < 1){
            params = Arrays.copyOf(params, 1);
        }
        CreateGameRequest request = new CreateGameRequest(params[0], sessionAuth);
        try{
            server.createGame(request);
            return "Game successfully created";
        } catch (DataAccessException e) {
            return e.getMessage();
        }
    }

    private String listGames(String[] params){
        ListGamesRequest request = new ListGamesRequest(sessionAuth);
        try{
            ListGamesResponse response = server.listGames(request);
            StringBuilder sb = new StringBuilder();
            int i = 1;
            for (GameDataResponse game : response.games()){
                sb.append(String.format("%d. %s, White Player: %s, Black Player: %s\n", i,
                        game.gameName(),
                        (game.whiteUsername()!=null) ? game.whiteUsername() : "None",
                        (game.blackUsername()!=null) ? game.blackUsername() : "None"));
                gameMap.put(String.valueOf(i++), game.gameID());
            }
            return sb.toString();
        } catch (DataAccessException e) {
            return e.getMessage();
        }
    }

    private String joinGame(String[] params){
        if (params.length < 2){
            params = Arrays.copyOf(params, 2);
        }
        Integer id = gameMap.get(params[0]);
        if (id==null){
            return "Error: ID must be a number and correspond to a game in the game list";
        }
        String playerColor = (params[1] != null) ? params[1].toUpperCase() : null;
        JoinGameRequest request = new JoinGameRequest(playerColor, id, sessionAuth);
        try{
            server.joinGame(request);
            state = State.GAMEPLAY;
            ChessGame.TeamColor sessionColor = (Objects.equals(playerColor, "WHITE")) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            gameplayClient.connect(sessionAuth, id, sessionColor, Connect.ConnectorType.PLAYER);
            return "";
        } catch (DataAccessException e){
            return e.getMessage();
        }
    }

    private String observeGame(String[] params){
        if (params.length < 1){
            params = Arrays.copyOf(params, 1);
        }
        Integer id = gameMap.get(params[0]);
        if (id==null){
            return "Error: ID must be a number and correspond to a game in the game list";
        }
        try {
            state = State.GAMEPLAY;
            gameplayClient.connect(sessionAuth, id, ChessGame.TeamColor.WHITE, Connect.ConnectorType.OBSERVER);
            return"";
        } catch(DataAccessException e){
            return e.getMessage();
        }
    }

    private String displayGame(Integer id, ChessGame.TeamColor color){
        ChessGame game = new ChessGame();
        DisplayGame display = new DisplayGame(game, color);
        display.displayGame();
        return "";
    }

    private String leaveGame(){
        gameplayClient.leaveGame();
        state = State.LOGGEDIN;
        return "";
    }




}

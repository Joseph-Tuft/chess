package client;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import ui.DisplayGame;
import websocket.commands.Connect;
import websocket.messages.LoadGame;
import websocket.messages.ServerMessage;

import java.util.Arrays;

public class GameplayClient implements ServerMessageObserver {

    private final WebSocketFacade ws;
    private String sessionAuth;
    private ChessGame.TeamColor sessionColor = ChessGame.TeamColor.WHITE;
    private int sessionID;
    private ChessGame currentGame = new ChessGame();

    private final Gson SERIALIZER = new Gson();

    public GameplayClient(String serverUrl) throws DataAccessException {
        ws = new WebSocketFacade(serverUrl, this);
    }

    public void notify(ServerMessage serverMessage){
        if (serverMessage instanceof LoadGame loadGame){
            currentGame = SERIALIZER.fromJson(loadGame.getMessage(), ChessGame.class);
            DisplayGame display = new DisplayGame(currentGame, sessionColor);
            display.displayGame();
        }
        System.out.print(serverMessage.getMessage());
    };

    public void connect(String authToken, int gameID, ChessGame.TeamColor color, Connect.ConnectorType connector){
        sessionAuth = authToken;
        sessionID = gameID;
        sessionColor = color;
        ws.connect(authToken, gameID, connector);
    }

    private String displayGame(Integer id, ChessGame.TeamColor color){
        ChessGame game = new ChessGame();
        DisplayGame display = new DisplayGame(game, color);
        display.displayGame();
        return "";
    }

    public void resign(){
        ws.resign(sessionAuth, sessionID);
    }

    public void redraw(){
        DisplayGame display = new DisplayGame(currentGame, sessionColor);
        display.displayGame();
    }

    public String highlight(String params[]){
        if (params.length < 1){
            params = Arrays.copyOf(params, 1);
        }
        if (params[0] == null){
            return "Error: the position must be of the format [a2]";
        }
        DisplayGame display = new DisplayGame(currentGame, sessionColor);
        //display.highlightGame();
    }




}

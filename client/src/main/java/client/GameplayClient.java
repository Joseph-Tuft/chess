package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import ui.DisplayGame;
import websocket.commands.Connect;
import websocket.messages.LoadGame;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLACK;
import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;

public class GameplayClient implements ServerMessageObserver {

    private final WebSocketFacade ws;
    private String sessionAuth;
    private ChessGame.TeamColor sessionColor = ChessGame.TeamColor.WHITE;
    private int sessionID;
    private ChessGame currentGame = new ChessGame();

    private static final Gson SERIALIZER = new Gson();
    Scanner scanner = new Scanner(System.in);

    public GameplayClient(String serverUrl) throws DataAccessException {
        ws = new WebSocketFacade(serverUrl, this);
    }

    public void notify(ServerMessage serverMessage){
        if (serverMessage instanceof LoadGame loadGame){
            currentGame = SERIALIZER.fromJson(loadGame.getMessage(), ChessGame.class);
            System.out.println();
            DisplayGame display = new DisplayGame(currentGame, sessionColor);
            display.displayGame();
            if (currentGame.gameStatus == 0) {
                System.out.println(SET_TEXT_COLOR_BLACK + String.format("%s's turn",
                        (currentGame.turn.equals(ChessGame.TeamColor.WHITE) ? "White" : "Black")));
            }
        } else {
            System.out.println(SET_TEXT_COLOR_BLACK + serverMessage.getMessage());
        }
    };

    public void connect(String authToken, int gameID, ChessGame.TeamColor color, Connect.ConnectorType connector){
        sessionAuth = authToken;
        sessionID = gameID;
        sessionColor = color;
        ws.connect(authToken, gameID, connector);
    }

    public String resign(){
        while(true) {
            System.out.println("Are you sure you want to resign? [y/n]");
            if (!scanner.hasNextLine()) {
                return "Input stream closed. Action aborted.";
            }
            String line = scanner.nextLine();
            if (line.isEmpty()) {
                continue;
            }
            String response = line.toLowerCase();
            if (response.charAt(0) == 'y') {
                ws.resign(sessionAuth, sessionID);
                return "";
            } else if (response.charAt(0) == 'n') {
                return "";
            } else {
                System.out.println("Invalid response. please type 'y' or 'n'");
            }
        }
    }

    public String redraw(){
        DisplayGame display = new DisplayGame(currentGame, sessionColor);
        display.displayGame();
        return "";
    }

    public ChessPosition getPos(String pos) throws BadRequestException {
        if (pos == null || pos.length() != 2){
            throw new BadRequestException("Error: the position must be of the format ex: a2");
        }
        if(pos.charAt(0) < 'a' || pos.charAt(0) > 'h' || pos.charAt(1) < '1' || pos.charAt(1) > '8'){
            throw new BadRequestException("Error: the position must be of the format ex: a2");
        }
        int col = pos.charAt(0) - 'a' + 1;
        int row = pos.charAt(1) - '0';
        return new ChessPosition(row, col);
    }

    public String highlight(String params[]){
        if (params.length < 1){
            params = Arrays.copyOf(params, 1);
        }
        try {
            ChessPosition pos = getPos(params[0]);
            DisplayGame display = new DisplayGame(currentGame, sessionColor);
            display.highlightGame(pos);
            return "";
        } catch(DataAccessException e){
            return e.getMessage();
        }
    }

    public ChessPiece.PieceType getPromotion(String promote) throws BadRequestException {
        if (promote == null){
            return null;
        }
        promote = promote.toLowerCase();
        return switch (promote){
            case "queen" -> ChessPiece.PieceType.QUEEN;
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            default -> throw new BadRequestException("Error: Invalid promotion piece");
        };
    }

    public String makeMove(String params[]){
        if (params.length < 4){
            params = Arrays.copyOf(params, 4);
        }
        try {
            ChessPosition startPos = getPos(params[0]);
            ChessPosition endPos = getPos(params[2]);
            ChessMove move = new ChessMove(startPos, endPos, getPromotion(params[3]));
            ws.makeMove(sessionAuth, sessionID, move);
            return "";
        } catch (DataAccessException e){
            return e.getMessage();
        }
    }

    public String leaveGame(){
        ws.leave(sessionAuth, sessionID);
        return "";
    }
}

package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessSelector;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.exceptions.DataAccessException;
import io.javalin.websocket.*;
import model.GameData;
import websocket.commands.Connect;
import websocket.commands.MakeMove;
import websocket.commands.UserGameCommand;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final UserDAO userDAO = DataAccessSelector.getUserDAO();
    private final AuthDAO authDAO = DataAccessSelector.getAuthDAO();
    private final GameDAO gameDAO = DataAccessSelector.getGameDAO();
    private static Gson SERIALIZER = new Gson();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> {
                    Connect connectCmd = new Gson().fromJson(ctx.message(), Connect.class);
                    connect(connectCmd.getAuthToken(),
                            connectCmd.getGameID(),
                            connectCmd.getConnector(),
                            ctx.session);
                }
                case MAKE_MOVE -> {
                    MakeMove moveCmd = new Gson().fromJson(ctx.message(), MakeMove.class);
                    makeMove(moveCmd.getAuthToken(), moveCmd.getGameID(), moveCmd.getMove(), ctx.session);
                }
                case LEAVE -> leave(command.getAuthToken(), command.getGameID(), ctx.session);
                case RESIGN -> resign(command.getAuthToken(), command.getGameID(), ctx.session);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(String authToken, Integer gameID, Connect.ConnectorType connector, Session session) throws IOException{
        connections.add(gameID, session);
        try {
            String username = authDAO.getAuthFromAuth(authToken).username();
            String message;

            ChessGame game = gameDAO.getGame(gameID).game();
            String gameJson = SERIALIZER.toJson(game);

            LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, gameJson);
            connections.reply(session, loadGame);

            if (connector == Connect.ConnectorType.PLAYER) {
                message = String.format("%s has joined the game.", username);
            } else {
                message = String.format("%s is observing the game.", username);
            }
            connections.broadcast(gameID, session, new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message));
        } catch (DataAccessException e){
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            connections.reply(session, errorMessage);
        }
    }

    private void checkCheckMate(int gameID, ChessGame game)throws IOException{
        // Broadcast checkmate and update game to game over
        Notification announceCheckmate;
        if (game.isInCheckmate(ChessGame.TeamColor.WHITE)){
            game.gameStatus = 1;
            gameDAO.makeMove(gameID, game);
            announceCheckmate = new Notification(ServerMessage.ServerMessageType.NOTIFICATION,
                    String.format("White is in checkmate, Black has won."));
            connections.broadcast(gameID, null, announceCheckmate);
        } else if (game.isInCheckmate(ChessGame.TeamColor.BLACK)){
            game.gameStatus = 1;
            gameDAO.makeMove(gameID, game);
            announceCheckmate = new Notification(ServerMessage.ServerMessageType.NOTIFICATION,
                    String.format("Black is in checkmate, White has won."));
            connections.broadcast(gameID, null, announceCheckmate);
        }
    }

    private void makeMove(String authToken, int gameID, ChessMove move, Session session) throws IOException , DataAccessException {
        GameData gameData;
        ChessGame game;
        String username;

        // Get data and validate arguments
        try {
            gameData = gameDAO.getGame(gameID);
            game = gameData.game();
            username = authDAO.getAuthFromAuth(authToken).username();
        } catch (DataAccessException e){
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            connections.reply(session, errorMessage);
            return;
        }

        // Make sure game hasn't ended
        if (game.gameStatus == 1){
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Game is over");
            connections.reply(session, errorMessage);
            return;
        }

        // Try the move
        try {
            if (username.equals(gameData.blackUsername()) && game.turn.equals(ChessGame.TeamColor.WHITE)){
                throw new InvalidMoveException("Error: Not your turn");
            } else if (username.equals(gameData.whiteUsername()) && game.turn.equals(ChessGame.TeamColor.BLACK)) {
                throw new InvalidMoveException("Error: Not your turn");
            } else if (!username.equals(gameData.blackUsername()) && !username.equals(gameData.whiteUsername())){
                throw new InvalidMoveException("Error: you are observing. You cannot make moves");
            }
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            connections.reply(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage()));
            return;
        }

        gameDAO.makeMove(gameID, game);

        // Broadcast the game
        String gameJson = SERIALIZER.toJson(game);
        connections.broadcast(gameID, null, new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, gameJson));

        // Broadcast the move
        Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION,
                String.format("%s made the move %s", username, move));
        connections.broadcast(gameID, session, notification);

        // Broadcast checkmate and update game to game over
        checkCheckMate(gameID, game);
    }

    private void leave(String authToken, Integer gameID, Session session) throws IOException{
        String username = authDAO.getAuthFromAuth(authToken).username();
        gameDAO.leaveGame(username, gameID);
        connections.remove(gameID, session);
        connections.broadcast(gameID,
                null,
                new Notification(ServerMessage.ServerMessageType.NOTIFICATION, String.format("%s has left the game", username)));
    }

    private void resign(String authToken, Integer gameID, Session session) throws IOException{
        try {
            String username = authDAO.getAuthFromAuth(authToken).username();
            GameData gameData = gameDAO.getGame(gameID);
            ChessGame game = gameData.game();

            if(game.gameStatus == 1){
                throw new InvalidMoveException("Error: Game is already over");
            }
            if(!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())){
                throw new InvalidMoveException("Error: Observer cannot resign");
            }

            game.gameStatus = 1;
            gameDAO.makeMove(gameID, game);
            Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION,
                    String.format("%s has resigned", username));
            connections.broadcast(gameID, null, notification);
        } catch (DataAccessException | InvalidMoveException e){
            connections.reply(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage()));
        }
    }


}

package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessSelector;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
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
        String username = authDAO.getAuthFromAuth(authToken).username();
        String message;
        if (connector.equals(Connect.ConnectorType.PLAYER)) {
            message = String.format("%s has joined the game.");
        } else {
            message = String.format("%s is observing the game.");
        }
        connections.broadcast(gameID, session, new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message));
    }

    private void makeMove(String authToken, Integer gameID, ChessMove move, Session session) throws IOException{
        ChessGame game = gameDAO.getGame(gameID).game();
        if (game.gameStatus.equals(1)){
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Game is over");
            connections.reply(session, errorMessage);
        } else {
            try {
                game.makeMove(move);
            } catch (InvalidMoveException e) {
                connections.reply(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage()));
            }
            gameDAO.makeMove(gameID, game);
            String gameJson = SERIALIZER.toJson(game);
            connections.broadcast(gameID, null, new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, gameJson));
        }
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
        String username = authDAO.getAuthFromAuth(authToken).username();
        ChessGame game = gameDAO.getGame(gameID).game();
        game.gameStatus = 1;
        Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION,
                String.format("%s has resigned", username));
        connections.broadcast(gameID, null, notification);
    }


}

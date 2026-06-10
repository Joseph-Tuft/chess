package client;

import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import jakarta.websocket.*;
import websocket.commands.Connect;
import websocket.commands.MakeMove;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    private final Gson SERIALIZER = new Gson();
    private ServerMessageObserver messageObserver;

    WebSocketFacade(String url, ServerMessageObserver messageObserver) throws DataAccessException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.messageObserver = messageObserver;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = SERIALIZER.fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()){
                        case NOTIFICATION -> {
                            Notification notification = SERIALIZER.fromJson(message, Notification.class);
                            messageObserver.notify(notification);
                        }
                        case ERROR -> {
                            ErrorMessage errorMessage = SERIALIZER.fromJson(message, ErrorMessage.class);
                            messageObserver.notify(errorMessage);
                        }
                        case LOAD_GAME -> {
                            LoadGame loadGame = SERIALIZER.fromJson(message, LoadGame.class);
                            messageObserver.notify(loadGame);
                        }
                    }
                }
            });
        } catch(IOException | DeploymentException | URISyntaxException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, int gameID, Connect.ConnectorType connector) throws DataAccessException{
        try {
            Connect connectCommand = new Connect(UserGameCommand.CommandType.CONNECT, authToken, gameID, connector);
            this.session.getBasicRemote().sendText(SERIALIZER.toJson(connectCommand));
        }
        catch(IOException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws DataAccessException{
        try {
            MakeMove moveCommand = new MakeMove(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
            this.session.getBasicRemote().sendText(SERIALIZER.toJson(moveCommand));
        }
        catch(IOException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    public void leave(String authToken, int gameID) throws DataAccessException{
        try{
            UserGameCommand leaveCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(SERIALIZER.toJson(leaveCommand));
        }
        catch (IOException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    public void resign(String authToken, int gameID) throws DataAccessException{
        try{
            UserGameCommand resignCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(SERIALIZER.toJson(resignCommand));
        }
        catch (IOException e){
            throw new DataAccessException(e.getMessage());
        }
    }
}

package client;

import websocket.messages.ServerMessage;

public interface ServerMessageObserver {
    public void notify(ServerMessage serverMessage);
}

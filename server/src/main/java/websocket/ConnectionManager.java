package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
public class ConnectionManager {
    public static final ConcurrentHashMap<Integer, List<Session>> connections = new ConcurrentHashMap<>();
    private static Gson SERIALIZER = new Gson();

    public void add(Integer gameId, Session session) {
        if (connections.containsKey(gameId)){
            connections.get(gameId).add(session);
        }
        else {
            connections.put(gameId, new ArrayList<>());
            connections.get(gameId).add(session);
        }
    }

    public void remove(Integer gameId, Session session) {
        connections.get(gameId).remove(session);
    }

    public void broadcast(Integer gameId, Session excludeSession, ServerMessage message) throws IOException {
        String messageJson = SERIALIZER.toJson(message);
        for (Session c : connections.get(gameId)) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(messageJson);
                }
            }
        }
    }

    public void reply(Session session, ServerMessage message) throws IOException {
        String messageJson = SERIALIZER.toJson(message);
        session.getRemote().sendString(messageJson);
    }
}
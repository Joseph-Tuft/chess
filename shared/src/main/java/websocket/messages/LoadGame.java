package websocket.messages;

public class LoadGame extends ServerMessage{

    String game;

    public LoadGame (ServerMessageType type, String game){
        super(type);
        this.game = game;
    }

    @Override
    public String getMessage(){
        return this.game;
    }
}

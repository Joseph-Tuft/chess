package websocket.commands;


public class Connect extends UserGameCommand {
    ConnectorType connector;

    public Connect (UserGameCommand.CommandType commandType, String authToken, Integer gameID, ConnectorType connector){
        super(commandType, authToken, gameID);
        this.connector = connector;
    }

    public ConnectorType getConnector() {
        return this.connector;
    }

    public enum ConnectorType{
        PLAYER,
        OBSERVER
    }
}

package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.GameData;

public class SQLGameDAO implements GameDAO{
    public void createGame(GameData g) throws DataAccessException{
        String statement = "INSERT INTO ";
    };
    public GameData getGame(int gameID) throws DataAccessException{
        return null;
    };
    public void updateGame(String username, String playerColor, int gameID) throws DataAccessException{

    };
    public void clearGames() throws DataAccessException{
        String statement = "TRUNCATE TABLE gameList";
        DatabaseManager.executeUpdate(statement);
    };
}

package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public class GameDAO {

    private static ArrayList<GameData> gameList = new ArrayList<>();
    public static int numGames = 1;

    public void createGame(GameData g) throws DataAccessException{
        gameList.add(g);
        numGames++;
    }

    public GameData getGame(int gameID) throws DataAccessException{
        return null;
    }

    public GameData getGame(String gameName) throws DataAccessException{
        return null;
    }

    public void updateGame(String playerColor, int gameID) throws DataAccessException{

    }

    public void deleteGame(String gameName) throws DataAccessException{

    }

    public void deleteGame(int gameID) throws DataAccessException{

    }

    public void clearGames() throws DataAccessException{
        gameList.clear();
    }
}

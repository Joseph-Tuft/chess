package dataaccess;

import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import model.GameData;

import java.util.ArrayList;
import java.util.List;

public class MemoryGameDAO implements GameDAO{

    public static final ArrayList<GameData> GAME_LIST = new ArrayList<>();
    public static int numGames = 1;

    public int createGame(GameData g) throws DataAccessException {
        GameData newGame= new GameData(numGames, g.whiteUsername(), g.blackUsername(), g.gameName(), g.game());
        GAME_LIST.add(newGame);
        numGames++;
        return newGame.gameID();
    }

    public GameData getGame(int gameID) throws DataAccessException{
        for (GameData game : GAME_LIST){
            if (gameID == game.gameID()){
                return game;
            }
        }
        throw new BadRequestException("Error: bad request");
    }

    public ArrayList<List<String>> getGames() throws DataAccessException{
        ArrayList<List<String>> returnStatement = new ArrayList<List<String>>();
        for (GameData game : GAME_LIST){
            returnStatement.add(List.of(String.valueOf(game.gameID()), game.whiteUsername(), game.blackUsername(), game.gameName()));
        }
        return returnStatement;
    }

    public void updateGame(String username, String playerColor, int gameID) throws DataAccessException{
        GameData tempGame = getGame(gameID);

        final GameData game;

        if(playerColor.equals("WHITE") && tempGame.whiteUsername() == null){
            game = new GameData(tempGame.gameID(), username, tempGame.blackUsername(), tempGame.gameName(), tempGame.game());
        } else if (playerColor.equals("BLACK") && tempGame.blackUsername() == null){
            game = new GameData(tempGame.gameID(), tempGame.whiteUsername(), username, tempGame.gameName(), tempGame.game());
        } else if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")){
            throw new BadRequestException("Error: bad request");
        } else {throw new AlreadyTakenException("Error: already taken");}

        GAME_LIST.remove(tempGame);
        GAME_LIST.add(game);
    }

    public void clearGames() throws DataAccessException{
        GAME_LIST.clear();
    }
}

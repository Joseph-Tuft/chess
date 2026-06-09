package dataaccess;

import chess.ChessGame;
import dataaccess.exceptions.DataAccessException;
import model.GameData;

import java.util.ArrayList;
import java.util.List;

public interface GameDAO {
    public int createGame(GameData g) throws DataAccessException;
    public GameData getGame(int gameID) throws DataAccessException;
    public ArrayList<List<String>> getGames() throws DataAccessException;
    public void joinGame(String username, String playerColor, int gameID) throws DataAccessException;
    public void clearGames() throws DataAccessException;
    public void makeMove(Integer gameID, ChessGame game);
}

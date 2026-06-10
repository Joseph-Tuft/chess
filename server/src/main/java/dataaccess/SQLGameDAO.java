package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SQLGameDAO implements GameDAO{
    private static final Gson SERIALIZER = new Gson();

    public int createGame(GameData g) throws DataAccessException{

        var gameJson = SERIALIZER.toJson(g.game());
        String statement = "INSERT INTO gameList (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        return (DatabaseManager.executeUpdate(statement, g.whiteUsername(), g.blackUsername(), g.gameName(), gameJson));
    };

    public GameData getGame(int gameID) throws DataAccessException{
        String statement = "SELECT whiteUsername, blackUsername, gameName, game FROM gameList WHERE gameID = ?";
        try(Connection conn = DatabaseManager.getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setInt(1, gameID);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    String whiteUsername = rs.getString("whiteUsername");
                    String blackUsername = rs.getString("blackUsername");
                    String gameName = rs.getString("gameName");
                    String game = rs.getString("game");

                    ChessGame chessGame = new Gson().fromJson(game, ChessGame.class);

                    return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                }
            }
        } catch (SQLException e){
            throw new BadRequestException(String.format("%s", e));
        }
        throw new BadRequestException("Error: unauthorized (user)");
    };

    public ArrayList<List<String>> getGames() throws DataAccessException{
        String statement = "SELECT gameID, whiteUsername, blackUsername, gameName FROM gameList";
        ArrayList<List<String>> returnStatement = new ArrayList<List<String>>();
        try(Connection conn = DatabaseManager.getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    String gameID = String.valueOf(rs.getInt("gameID"));
                    String whiteUsername = rs.getString("whiteUsername");
                    String blackUsername = rs.getString("blackUsername");
                    String gameName = rs.getString("gameName");

                    returnStatement.add(Arrays.asList(gameID, whiteUsername, blackUsername, gameName));
                }
                return returnStatement;
            }
        } catch (SQLException e){
            throw new BadRequestException(String.format("%s", e));
        }
    }

    public void joinGame(String username, String playerColor, int gameID) throws DataAccessException{
        GameData tempGame = getGame(gameID);

        String command = "UPDATE gameList SET whiteUsername = ?, blackUsername = ? WHERE gameID = ?";

        if(playerColor.equals("WHITE") && tempGame.whiteUsername() == null){
            DatabaseManager.executeUpdate(command, username, tempGame.blackUsername(), gameID);
        } else if (playerColor.equals("BLACK") && tempGame.blackUsername() == null){
            DatabaseManager.executeUpdate(command, tempGame.whiteUsername(), username, gameID);
        } else if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")){
            throw new BadRequestException("Error: bad request");
        } else {throw new AlreadyTakenException("Error: already taken");}
    }

    public void makeMove(Integer gameID, ChessGame game) throws DataAccessException{
        var gameJson = SERIALIZER.toJson(game);

        String command = "UPDATE gameList SET game = ? WHERE gameID = ?";

        DatabaseManager.executeUpdate(command, gameJson, gameID);
    }

    public void leaveGame(String username, int gameID) throws DataAccessException{
        GameData tempGame = getGame(gameID);
        String command = "UPDATE gameList SET whiteUsername = ?, blackUsername = ? WHERE gameID = ?";

        if(username.equals(tempGame.whiteUsername())){
            DatabaseManager.executeUpdate(command, null, tempGame.blackUsername(), gameID);
        } else if (username.equals(tempGame.blackUsername())){
            DatabaseManager.executeUpdate(command, tempGame.whiteUsername(), null, gameID);
        }
    }

    public void clearGames() throws DataAccessException{
        String statement = "TRUNCATE TABLE gameList";
        DatabaseManager.executeUpdate(statement);
    };
}

package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.UnauthorizedRequestException;
import model.GameData;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLGameDAO implements GameDAO{
    public void createGame(GameData g) throws DataAccessException{
        var serializer = new Gson();
        var gameJson = serializer.toJson(g.game());
        String statement = "INSERT INTO gameList (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        DatabaseManager.executeUpdate(statement, g.whiteUsername(), g.blackUsername(), g.gameName(), gameJson);
    };

    public GameData getGame(int gameID) throws DataAccessException{
        String statement = "SELECT whiteUsername, blackUsername, gameName, game FROM gameList WHERE id = ?";
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
        throw new UnauthorizedRequestException("Error: unauthorized (user)");
    };

    public void updateGame(String username, String playerColor, int gameID) throws DataAccessException{

    };

    public void clearGames() throws DataAccessException{
        String statement = "TRUNCATE TABLE gameList";
        DatabaseManager.executeUpdate(statement);
    };
}

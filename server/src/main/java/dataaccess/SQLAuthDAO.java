package dataaccess;

import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.UnauthorizedRequestException;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO{
    public void createAuth(AuthData a) throws DataAccessException{
        String statement = "INSERT INTO authList (authToken, username) VALUES (?, ?)";
        DatabaseManager.executeUpdate(statement, a.authToken(), a.username());
    };

    public AuthData getAuthFromAuth(String authToken) throws DataAccessException{
        String statement = "SELECT username FROM authList WHERE authToken = ?";
        try(Connection conn = DatabaseManager.getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setString(1, authToken);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    String username = rs.getString("username");
                    AuthData auth = new AuthData(authToken, username);
                    return auth;
                }
            }
        } catch (SQLException e){
            throw new BadRequestException(String.format("%s", e));
        }
        throw new UnauthorizedRequestException("Error: unauthorized (user)");
    };

    public AuthData getAuthFromUser(String username) throws DataAccessException{
        String statement = "SELECT authToken FROM authList WHERE username = ?";
        try(Connection conn = DatabaseManager.getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    String authToken = rs.getString("authToken");
                    AuthData auth = new AuthData(authToken, username);
                    return auth;
                }
            }
        } catch (SQLException e){
            throw new BadRequestException(String.format("%s", e));
        }
        throw new UnauthorizedRequestException("Error: unauthorized (user)");
    };

    public void deleteAuth(AuthData auth) throws DataAccessException{
        String statement = "DELETE FROM authList WHERE authToken = ?";
        DatabaseManager.executeUpdate(statement, auth.authToken());
    };

    public void clearAuths() throws DataAccessException{
        String statement = "TRUNCATE TABLE authList";
        DatabaseManager.executeUpdate(statement);
    };

}

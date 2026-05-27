package dataaccess;

import dataaccess.exceptions.*;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO{
    public void createUser(UserData u) throws DataAccessException{
        String statement = "INSERT INTO userList (username, password, email) VALUES (?, ?, ?)";
        try {
            DatabaseManager.executeUpdate(statement, u.username(), u.password(), u.email());
        } catch (ResponseException e){
            throw new AlreadyTakenException(String.format("Error: already taken: %s", e));
        }
    };

    public UserData getUser(String username) throws DataAccessException{
        String statement = "SELECT password, email FROM userList WHERE username = ?";
        try(Connection conn = DatabaseManager.getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    String password = rs.getString("password");
                    String email = rs.getString("email");
                    UserData user = new UserData(username, password, email);
                    return user;
                }
            }
        } catch (SQLException e){
            throw new BadRequestException(String.format("%s", e));
        }
        throw new UnauthorizedRequestException("Error: unauthorized (user)");
    };

    public void clearUsers() throws DataAccessException{
        String statement = "TRUNCATE TABLE userList";
        DatabaseManager.executeUpdate(statement);
    };
}

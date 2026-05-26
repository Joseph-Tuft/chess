package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO{
    public void createUser(UserData u) throws DataAccessException{

    };

    public UserData getUser(String username) throws DataAccessException{
        String statement = String.format("SELECT password, email FROM userList WHERE username = %s", username);
        try(Connection conn = DatabaseManager.getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    String password = rs.getString("password");
                    String email = rs.getString("email");
                    UserData user = new UserData(username, password, email);
                    return user;
                }
                else {return null;}
            }
        } catch (SQLException e){
            throw new DataAccessException(String.format("%s", e));
        }
    };

    public void clearUsers() throws DataAccessException{

    };
}

package dataaccess;

import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.ResponseException;
import model.AuthData;

import java.lang.module.ResolutionException;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO{
    public void createAuth(AuthData a) throws DataAccessException{
        String statement = "INSERT INTO authList (authToken, username) VALUES (?, ?)";
        DatabaseManager.executeUpdate(statement, a.authToken(), a.username());
    };

    public AuthData getAuthFromAuth(String authToken) throws DataAccessException{
        return null;
    };

    public AuthData getAuthFromUser(String username) throws DataAccessException{
        return null;
    };

    public void deleteAuth(AuthData auth) throws DataAccessException{

    };

    public void clearAuths() throws DataAccessException{

    };

}

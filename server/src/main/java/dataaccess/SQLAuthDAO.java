package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.AuthData;

public class SQLAuthDAO implements AuthDAO{
    public void createAuth(AuthData a) throws DataAccessException{

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

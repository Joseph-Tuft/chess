package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.UserData;

public class SQLUserDAO implements UserDAO{
    public void createUser(UserData u) throws DataAccessException{

    };

    public UserData getUser(String username) throws DataAccessException{
        return null;
    };

    public void clearUsers() throws DataAccessException{

    };
}

package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.UserData;

public interface UserDAO {
    public void createUser(UserData u) throws DataAccessException;
    public UserData getUser(String username) throws DataAccessException;
    public void clearUsers() throws DataAccessException;
}

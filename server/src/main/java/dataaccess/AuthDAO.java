package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.AuthData;

public interface AuthDAO {
    public void createAuth(AuthData a) throws DataAccessException;
    public AuthData getAuthFromAuth(String authToken) throws DataAccessException;
    public AuthData getAuthFromUser(String username) throws DataAccessException;
    public void deleteAuth(AuthData auth) throws DataAccessException;
    public void clearAuths() throws DataAccessException;
}

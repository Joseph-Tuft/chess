package dataaccess;

import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.UnauthorizedRequestException;
import model.AuthData;

import java.util.ArrayList;

public class AuthDAO {

    private static final ArrayList<AuthData> AUTH_LIST = new ArrayList<>();

    public void createAuth(AuthData a) throws DataAccessException {
        AUTH_LIST.add(a);
    }

    public AuthData getAuthFromAuth(String authToken) throws DataAccessException{
        for (AuthData auth : AUTH_LIST){
            if (auth.authToken().equals(authToken)){
                return auth;
            }
        }
        throw new UnauthorizedRequestException("Error: unauthorized (auth Token)");
    }

    public void deleteAuth(AuthData auth) throws DataAccessException{
        if (!AUTH_LIST.remove(auth)){
            throw new UnauthorizedRequestException("Error: unauthorized (auth delete)");
        }
    }

    public void clearAuths() throws DataAccessException{
        AUTH_LIST.clear();
    }
}

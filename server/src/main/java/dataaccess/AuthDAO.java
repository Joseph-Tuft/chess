package dataaccess;

import dataaccess.Exceptions.DataAccessException;
import dataaccess.Exceptions.UnauthorizedRequestException;
import model.AuthData;

import java.util.ArrayList;

public class AuthDAO {

    private static final ArrayList<AuthData> authList = new ArrayList<>();

    public void createAuth(AuthData a) throws DataAccessException {
        authList.add(a);
    }

    public AuthData getAuthFromAuth(String authToken) throws DataAccessException{
        for (AuthData auth : authList){
            if (auth.authToken().equals(authToken)){
                return auth;
            }
        }
        throw new UnauthorizedRequestException("Error: unauthorized (auth Token)");
    }

    public void deleteAuth(AuthData auth) throws DataAccessException{
        if (!authList.remove(auth)){
            throw new UnauthorizedRequestException("Error: unauthorized (auth delete)");
        }
    }

    public void clearAuths() throws DataAccessException{
        authList.clear();
    }
}

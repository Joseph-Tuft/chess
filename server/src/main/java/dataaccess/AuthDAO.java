package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;

public class AuthDAO {

    private static ArrayList<AuthData> authList = new ArrayList<>();

    public void createAuth(AuthData a) throws DataAccessException{
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

    public AuthData getAuthFromUser(String username) throws DataAccessException{
        for (AuthData auth : authList){
            if (auth.username().equals(username)){
                return auth;
            }
        }
        throw new UnauthorizedRequestException("Error: unauthorized (auth username)");
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

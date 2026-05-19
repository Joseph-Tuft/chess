package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public class AuthDAO {

    private static ArrayList<AuthData> authList = new ArrayList<>();

    public void createAuth(AuthData a) throws DataAccessException{

    }

    public AuthData getAuth(String authToken) throws DataAccessException{
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException{

    }

    public void clearAuths() throws DataAccessException{
        authList.clear();
    }
}

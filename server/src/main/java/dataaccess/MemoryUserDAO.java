package dataaccess;

import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.UnauthorizedRequestException;
import model.UserData;

import java.util.ArrayList;

public class MemoryUserDAO implements UserDAO{

    private static final ArrayList<UserData> USER_LIST = new ArrayList<>();

    public void createUser(UserData u) throws DataAccessException {
        if (USER_LIST.contains(u)){
            throw new AlreadyTakenException("Error: already taken");
        }
        else {USER_LIST.add(u);}
    }

    public UserData getUser(String username) throws DataAccessException{
        for (UserData user : USER_LIST){
            if (user.username().equals(username)){
                return user;
            }
        }
        throw new UnauthorizedRequestException("Error: unauthorized (user)");
    }

    public void clearUsers() throws DataAccessException{
        USER_LIST.clear();
    }

}

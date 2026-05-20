package dataaccess;

import dataaccess.Exceptions.AlreadyTakenException;
import dataaccess.Exceptions.DataAccessException;
import dataaccess.Exceptions.UnauthorizedRequestException;
import model.UserData;

import java.util.ArrayList;

public class UserDAO {

    private static ArrayList<UserData> userList = new ArrayList<>();

    public void createUser(UserData u) throws DataAccessException {
        if (userList.contains(u)){
            throw new AlreadyTakenException("Error: already taken");
        }
        else userList.add(u);
    }

    public UserData getUser(String username) throws DataAccessException{
        for (UserData user : userList){
            if (user.username().equals(username)){
                return user;
            }
        }
        throw new UnauthorizedRequestException("Error: unauthorized (user)");
    }

    public void deleteUser(String username) throws DataAccessException{

    }

    public void clearUsers() throws DataAccessException{
        userList.clear();
    }

}

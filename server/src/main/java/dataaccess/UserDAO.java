package dataaccess;

import model.UserData;

import java.util.ArrayList;

public class UserDAO {

    private static ArrayList<UserData> userList = new ArrayList<>();

    public void createUser(UserData u) throws DataAccessException{
        if (userList.contains(u)){
            throw new DataAccessException("Error: already taken");
        }
        else userList.add(u);
    }

    public UserData getUser(String username) throws DataAccessException{
        return null;
    }

    public void deleteUser(String username) throws DataAccessException{

    }

    public void clearUsers() throws DataAccessException{
        userList.clear();
    }

}

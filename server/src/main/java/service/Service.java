package service;
import java.util.UUID;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import model.*;

public class Service {
    private UserDAO userDAO = new UserDAO();
    private AuthDAO authDAO = new AuthDAO();
    private GameDAO gameDAO = new GameDAO();

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public RegisterResponse register(RegisterRequest request) throws DataAccessException {
        //Create userData object and add it to database
        UserData user = new UserData(request.username(), request.password(), request.email());
        userDAO.createUser(user);

        //Create authData object and add it to database
        String authToken = generateToken();
        AuthData auth = new AuthData(authToken, user.username());
        authDAO.createAuth(auth);

        //return response
        return new RegisterResponse(auth.authToken(), auth.username());
    }

    public void clear() throws DataAccessException {
        userDAO.clearUsers();
        authDAO.clearAuths();
        gameDAO.clearGames();
    }
}

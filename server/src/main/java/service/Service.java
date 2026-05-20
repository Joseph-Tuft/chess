package service;
import java.util.UUID;
import java.lang.reflect.Field;

import chess.ChessGame;
import dataaccess.*;
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

    public void checkNullFields(Object object) throws IncompleteRequestException{
        for (Field field : object.getClass().getDeclaredFields()){
            field.setAccessible(true);
            try {
                if (field.get(object) == null) {
                    throw new IncompleteRequestException("Error: bad request");
                }
            } catch (IllegalAccessException e) {
                throw new IncompleteRequestException("Error: bad request");
            }
        }
    }

    public RegisterResponse register(RegisterRequest request) throws DataAccessException {
        //Validate request
        checkNullFields(request);

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

    public LoginResponse login(LoginRequest request) throws DataAccessException {
        //Validate request
        checkNullFields(request);

        //Check user exists and get user and auth if they do
        UserData user = userDAO.getUser(request.username());
        String authToken = generateToken();
        AuthData auth = new AuthData(authToken, user.username());
        authDAO.createAuth(auth);

        //Validate the password
        if(!user.password().equals(request.password())){
            throw new UnauthorizedRequestException("Error: unauthorized");
        }

        //return response
        return new LoginResponse(auth.username(), auth.authToken());
    }

    public LogoutResponse logout(LogoutRequest request) throws DataAccessException{
        AuthData auth = authDAO.getAuthFromAuth(request.authToken());
        authDAO.deleteAuth(auth);
        return new LogoutResponse();
    }

    public CreateGameResponse createGame(CreateGameRequest request) throws DataAccessException{
        //Validate request
        checkNullFields(request);

        //Validate authoken and get authData
        AuthData auth = authDAO.getAuthFromAuth(request.authToken());

        int ID = GameDAO.numGames;
        GameData game = new GameData(GameDAO.numGames, null, null, request.gameName(), new ChessGame());
        return new CreateGameResponse(game.gameID());
    }

    public void clear() throws DataAccessException {
        userDAO.clearUsers();
        authDAO.clearAuths();
        gameDAO.clearGames();
    }


}

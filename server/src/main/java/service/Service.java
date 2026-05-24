package service;
import java.util.ArrayList;
import java.util.UUID;
import java.lang.reflect.Field;
import chess.ChessGame;
import dataaccess.*;
import dataaccess.exceptions.*;
import model.requests.*;
import model.responses.*;
import model.*;

public class Service {
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    private void checkNullFields(Object object) throws BadRequestException {
        for (Field field : object.getClass().getDeclaredFields()){
            field.setAccessible(true);
            try {
                if (field.get(object) == null) {
                    throw new BadRequestException("Error: bad request");
                }
            } catch (IllegalAccessException e) {
                throw new BadRequestException("Error: bad request");
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

    public EmptyRecord logout(LogoutRequest request) throws DataAccessException{
        AuthData auth = authDAO.getAuthFromAuth(request.authToken());
        authDAO.deleteAuth(auth);
        return new EmptyRecord();
    }

    public CreateGameResponse createGame(CreateGameRequest request) throws DataAccessException{
        //Validate request
        checkNullFields(request);

        //Validate authoken and get authData
        authDAO.getAuthFromAuth(request.authToken());

        GameData game = new GameData(MemoryGameDAO.numGames, null, null, request.gameName(), new ChessGame());
        gameDAO.createGame(game);
        return new CreateGameResponse(game.gameID());
    }

    public ListGamesResponse listGames(ListGamesRequest request) throws DataAccessException{
        //Validate request
        checkNullFields(request);

        //Validate authToken
        authDAO.getAuthFromAuth(request.authToken());

        //Make a list only containing information that will be sent in response
        ArrayList<GameDataResponse> responses = new ArrayList<>();
        for (GameData game : MemoryGameDAO.GAME_LIST){
            responses.add(new GameDataResponse(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
        }

        return new ListGamesResponse(responses);
    }

    public EmptyRecord joinGame(JoinGameRequest request) throws DataAccessException{
        //Validate request
        checkNullFields(request);

        //Validate authToken and get authData
        AuthData auth = authDAO.getAuthFromAuth(request.authToken());

        //Join game, check if color is already taken
        gameDAO.updateGame(auth.username(), request.playerColor(), request.gameID());
        return new EmptyRecord();
    }

    public EmptyRecord clear() throws DataAccessException {
        userDAO.clearUsers();
        authDAO.clearAuths();
        gameDAO.clearGames();
        return new EmptyRecord();
    }

}

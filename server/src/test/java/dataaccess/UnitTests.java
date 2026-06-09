package dataaccess;

import chess.ChessGame;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.UnauthorizedRequestException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.Service;


public class UnitTests {
    Service service = new Service();
    private final UserDAO userDAO = DataAccessSelector.getUserDAO();
    private final AuthDAO authDAO = DataAccessSelector.getAuthDAO();
    private final GameDAO gameDAO = DataAccessSelector.getGameDAO();


    @Test
    @DisplayName("Create Game Success")
    public void createGameSuccess() {
        gameDAO.clearGames();
        GameData game = new GameData(1, null, null, "JoesGame", new ChessGame());
        gameDAO.createGame(game);
        Assertions.assertEquals(gameDAO.getGame(1), game);
    }

    @Test
    @DisplayName("Create Game Fail")
    public void registerFail() {
        gameDAO.clearGames();
        GameData game = new GameData(1, null, null, null, new ChessGame());
        Assertions.assertThrows(ResponseException.class, () -> gameDAO.createGame(game));
    }

    @Test
    @DisplayName("Get Game Success")
    public void getGameSuccess() {
        gameDAO.clearGames();
        GameData game = new GameData(1, null, null, "JoesGame", new ChessGame());
        gameDAO.createGame(game);
        Assertions.assertEquals(game, gameDAO.getGame(1));
    }

    @Test
    @DisplayName("Get Game Fail")
    public void getGameFail() {
        gameDAO.clearGames();
        GameData game = new GameData(1, null, null, "JoesGame", new ChessGame());
        gameDAO.createGame(game);
        Assertions.assertThrows(BadRequestException.class, () -> gameDAO.getGame(2));
    }

    @Test
    @DisplayName("Get Games Success")
    public void getGamesSuccess() {
        gameDAO.clearGames();
        GameData game = new GameData(1, null, null, "JoesGame", new ChessGame());
        gameDAO.createGame(game);
        GameData game2 = new GameData(2, null, null, "JoesGame2", new ChessGame());
        gameDAO.createGame(game2);
        Assertions.assertEquals(2, gameDAO.getGames().size());
    }

    @Test
    @DisplayName("Get Games Fail")
    public void getGamesFail() {
        gameDAO.clearGames();
        Assertions.assertEquals(0, gameDAO.getGames().size());
    }

    @Test
    @DisplayName("Update Game Success")
    public void updateGameSuccess() {
        gameDAO.clearGames();
        GameData game = new GameData(1, null, null, "JoesGame", new ChessGame());
        gameDAO.createGame(game);
        gameDAO.joinGame("whitePlayer", "WHITE", game.gameID());
        Assertions.assertEquals("whitePlayer", gameDAO.getGame(game.gameID()).whiteUsername());
    }

    @Test
    @DisplayName("Update Game Fail")
    public void updateGameFail() {
        gameDAO.clearGames();
        GameData game = new GameData(1, null, null, "JoesGame", new ChessGame());
        gameDAO.createGame(game);

        Assertions.assertThrows(BadRequestException.class, () -> gameDAO.joinGame("whitePlayer", "WHTE", game.gameID()));
    }

    @Test
    @DisplayName("Clear Game Success")
    public void clearGameSuccess() {
        gameDAO.clearGames();
        GameData game = new GameData(1, null, null, "JoesGame", new ChessGame());
        gameDAO.createGame(game);
        gameDAO.clearGames();
        Assertions.assertEquals(0, gameDAO.getGames().size());
    }

    @Test
    @DisplayName("Create User Success")
    public void createUserSuccess() {
        userDAO.clearUsers();
        UserData user = new UserData("Joe", "12345", "joe@joe.com");
        userDAO.createUser(user);
        Assertions.assertEquals(user.email(), userDAO.getUser("Joe").email());
    }

    @Test
    @DisplayName("Create Users Fail")
    public void createUserFail() {
        userDAO.clearUsers();
        UserData user = new UserData("Joe", "12345", null);
        Assertions.assertThrows(AlreadyTakenException.class, () -> userDAO.createUser(user));
    }

    @Test
    @DisplayName("Get User Success")
    public void getUserSuccess() {
        userDAO.clearUsers();
        UserData user = new UserData("Joe", "12345", "joe@joe.com");
        userDAO.createUser(user);
        Assertions.assertEquals(user.username(), userDAO.getUser("Joe").username());
    }

    @Test
    @DisplayName("Get User Fail")
    public void getUserFail() {
        userDAO.clearUsers();
        UserData user = new UserData("Joe", "12345", "joe@joe.com");
        userDAO.createUser(user);
        Assertions.assertThrows(UnauthorizedRequestException.class, () -> userDAO.getUser("Joee"));
    }

    @Test
    @DisplayName("Clear Users")
    public void clearUsers() {
        userDAO.clearUsers();
        UserData user = new UserData("Joe", "12345", "joe@joe.com");
        userDAO.createUser(user);
        userDAO.clearUsers();
        Assertions.assertThrows(UnauthorizedRequestException.class, () -> userDAO.getUser("Joe"));
    }

    @Test
    @DisplayName("Create Auth Success")
    public void createAuthSuccess() {
        authDAO.clearAuths();
        AuthData auth = new AuthData("joeauth", "Joe");
        authDAO.createAuth(auth);
        Assertions.assertEquals(auth.username(), authDAO.getAuthFromAuth("joeauth").username());
    }

    @Test
    @DisplayName("Create Auth Fail")
    public void createAuthFail() {
        authDAO.clearAuths();
        AuthData auth = new AuthData("joeauth", null);
        Assertions.assertThrows(ResponseException.class, () -> authDAO.createAuth(auth));
    }

    @Test
    @DisplayName("Get Auth from Auth Success")
    public void getAuthFromAuthSuccess() {
        authDAO.clearAuths();
        AuthData auth = new AuthData("joeauth", "Joe");
        authDAO.createAuth(auth);
        Assertions.assertEquals(authDAO.getAuthFromAuth("joeauth").username(), auth.username());
    }

    @Test
    @DisplayName("Get Auth from Auth Fail")
    public void getAuthFromAuthFail() {
        authDAO.clearAuths();
        AuthData auth = new AuthData("joeauth", "Joe");
        authDAO.createAuth(auth);
        Assertions.assertThrows(UnauthorizedRequestException.class, () -> authDAO.getAuthFromAuth("joeauths"));
    }

    @Test
    @DisplayName("Get Auth from User Success")
    public void getAuthFromUserSuccess() {
        authDAO.clearAuths();
        AuthData auth = new AuthData("joeauth", "Joe");
        authDAO.createAuth(auth);
        Assertions.assertEquals(authDAO.getAuthFromUser("Joe").authToken(), auth.authToken());
    }

    @Test
    @DisplayName("Get Auth from User Fail")
    public void getAuthFromUserFail() {
        authDAO.clearAuths();
        AuthData auth = new AuthData("joeauth", "Joe");
        authDAO.createAuth(auth);
        Assertions.assertThrows(UnauthorizedRequestException.class, () -> authDAO.getAuthFromUser("joeauths"));
    }

    @Test
    @DisplayName("Delete Auth Success")
    public void deleteAuthSuccess() {
        authDAO.clearAuths();
        AuthData auth = new AuthData("joeauth", "Joe");
        authDAO.createAuth(auth);
        authDAO.deleteAuth(auth);
        Assertions.assertThrows(UnauthorizedRequestException.class, () -> authDAO.getAuthFromAuth("joeauth"));
    }

    @Test
    @DisplayName("Delete Auth Fail")
    public void deleteAuthFail() {
        authDAO.clearAuths();
        AuthData auth = new AuthData("joeauth", "Joe");
        authDAO.createAuth(auth);
        AuthData auth2 = new AuthData("joeauthh", "Joe");
        authDAO.deleteAuth(auth2);
        Assertions.assertNotNull(authDAO.getAuthFromAuth("joeauth"));
    }

    @Test
    @DisplayName("Clear Auths")
    public void clearAuths() {
        authDAO.clearAuths();
        AuthData auth = new AuthData("joeauth", "Joe");
        authDAO.createAuth(auth);
        authDAO.clearAuths();
        Assertions.assertThrows(UnauthorizedRequestException.class, () -> authDAO.getAuthFromAuth("joeauth"));
    }
}

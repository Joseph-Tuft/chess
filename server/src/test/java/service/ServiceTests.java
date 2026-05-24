package service;

import dataaccess.*;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.UnauthorizedRequestException;
import model.AuthData;
import model.requests.*;
import model.responses.*;
import org.junit.jupiter.api.*;


public class ServiceTests {
    Service service = new Service();
    public final MemoryUserDAO userDAO = new MemoryUserDAO();
    public final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    public final MemoryGameDAO gameDAO = new MemoryGameDAO();


    @Test
    @DisplayName("Register Success")
    public void registerSuccess(){
        service.clear();
        RegisterRequest request = new RegisterRequest("Joe", "12345", "joe@joe.com");
        service.register(request);
        Assertions.assertEquals(request.email(), userDAO.getUser(request.username()).email());
    }

    @Test
    @DisplayName("Register Bad Request")
    public void registerFail(){
        service.clear();
        RegisterRequest request = new RegisterRequest("Joe", "12345", null);
        Assertions.assertThrows(BadRequestException.class, () -> service.register(request));
    }

    @Test
    @DisplayName("Login Success")
    public void loginSuccess(){
        service.clear();
        RegisterRequest registerRequest = new RegisterRequest("Joe", "12345", "joe@joe.com");
        service.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("Joe", "12345");
        Assertions.assertDoesNotThrow(() -> service.login(loginRequest));
    }

    @Test
    @DisplayName("Login Fail not Registered")
    public void loginNotRegistered(){
        service.clear();
        LoginRequest loginRequest = new LoginRequest("joe", "12345");
        Assertions.assertThrows(UnauthorizedRequestException.class, () -> service.login(loginRequest));
    }

    @Test
    @DisplayName("Login Fail Wrong Password")
    public void loginWrongPassword(){
        service.clear();
        RegisterRequest registerRequest = new RegisterRequest("Joe", "12345", "joe@joe.com");
        service.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("joe", "12346");
        Assertions.assertThrows(UnauthorizedRequestException.class, () -> service.login(loginRequest));
    }

    @Test
    @DisplayName("Logout Success")
    public void logoutSuccess(){
        service.clear();
        RegisterRequest registerRequest = new RegisterRequest("Joe", "12345", "joe@joe.com");
        service.register(registerRequest);
        AuthData auth = authDAO.getAuthFromUser(registerRequest.username());
        String authToken = auth.authToken();
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        service.logout(logoutRequest);
        Assertions.assertThrows(UnauthorizedRequestException.class, () -> authDAO.getAuthFromAuth(authToken));
    }

    @Test
    @DisplayName("Logout Fail")
    public void logoutFail(){
        RegisterRequest registerRequest = new RegisterRequest("Joe", "12345", "joe@joe.com");
        service.register(registerRequest);
        LogoutRequest logoutRequest = new LogoutRequest("");
        Assertions.assertThrows(UnauthorizedRequestException.class, () -> service.logout(logoutRequest));
    }

    @Test
    @DisplayName("ListGames Success")
    public void listGamesSuccess(){
        service.clear();
        RegisterRequest registerRequest = new RegisterRequest("Joe", "12345", "joe@joe.com");
        service.register(registerRequest);
        AuthData auth = authDAO.getAuthFromUser(registerRequest.username());

        ListGamesRequest listGamesRequest = new ListGamesRequest(auth.authToken());
        Assertions.assertTrue(service.listGames(listGamesRequest).games().isEmpty());

        CreateGameRequest createGameRequest = new CreateGameRequest("Joes Game", auth.authToken());
        service.createGame(createGameRequest);
        Assertions.assertFalse(service.listGames(listGamesRequest).games().isEmpty());
    }

    @Test
    @DisplayName("ListGames Fail")
    public void listGamesFail(){
        service.clear();
        RegisterRequest registerRequest = new RegisterRequest("Joe", "12345", "joe@joe.com");
        service.register(registerRequest);

        ListGamesRequest listGamesRequest = new ListGamesRequest("");
        Assertions.assertThrows(UnauthorizedRequestException.class, () -> service.listGames(listGamesRequest));
    }

    @Test
    @DisplayName("CreateGames Success")
    public void createGamesSuccess(){
        service.clear();
        RegisterRequest registerRequest = new RegisterRequest("Joe", "12345", "joe@joe.com");
        service.register(registerRequest);
        AuthData auth = authDAO.getAuthFromUser(registerRequest.username());

        CreateGameRequest createGameRequest = new CreateGameRequest("Joes Game", auth.authToken());
        CreateGameResponse response = service.createGame(createGameRequest);

        Assertions.assertEquals(gameDAO.getGame(response.gameID()).gameName(), createGameRequest.gameName());
    }

    @Test
    @DisplayName("CreateGames Fail no Name Provided")
    public void createGamesFail() {
        service.clear();
        RegisterRequest registerRequest = new RegisterRequest("Joe", "12345", "joe@joe.com");
        service.register(registerRequest);
        AuthData auth = authDAO.getAuthFromUser(registerRequest.username());

        CreateGameRequest createGameRequest = new CreateGameRequest(null, auth.authToken());

        Assertions.assertThrows(BadRequestException.class, () -> service.createGame(createGameRequest));
    }

    @Test
    @DisplayName("JoinGame Success")
    public void joinGameSuccess(){
        service.clear();
        RegisterRequest registerRequest = new RegisterRequest("Joe", "12345", "joe@joe.com");
        service.register(registerRequest);

        AuthData joeAuth = authDAO.getAuthFromUser(registerRequest.username());
        CreateGameRequest createGameRequest = new CreateGameRequest("Joes Game", joeAuth.authToken());
        int gameID = service.createGame(createGameRequest).gameID();

        JoinGameRequest joeRequest = new JoinGameRequest("WHITE", gameID, joeAuth.authToken());
        Assertions.assertDoesNotThrow(() -> service.joinGame(joeRequest));

        RegisterRequest registerRequest2 = new RegisterRequest("Bob", "12346", "bob@bob.com");
        service.register(registerRequest2);
        AuthData bobAuth = authDAO.getAuthFromUser(registerRequest2.username());

        JoinGameRequest bobRequest = new JoinGameRequest("BLACK", gameID, bobAuth.authToken());
        Assertions.assertDoesNotThrow(() -> service.joinGame(bobRequest));
    }

    @Test
    @DisplayName("JoinGame Fail Bad Color")
    public void joinGameFailBadColor() {
        service.clear();
        RegisterRequest registerRequest = new RegisterRequest("Joe", "12345", "joe@joe.com");
        service.register(registerRequest);

        AuthData joeAuth = authDAO.getAuthFromUser(registerRequest.username());
        CreateGameRequest createGameRequest = new CreateGameRequest("Joes Game", joeAuth.authToken());
        int gameID = service.createGame(createGameRequest).gameID();

        JoinGameRequest joeRequest = new JoinGameRequest("WHIT", gameID, joeAuth.authToken());
        Assertions.assertThrows(BadRequestException.class, () -> service.joinGame(joeRequest));
    }

    @Test
    @DisplayName("JoinGame Fail Taken Color")
    public void joinGameFailTakenColor(){
        service.clear();
        RegisterRequest registerRequest = new RegisterRequest("Joe", "12345", "joe@joe.com");
        service.register(registerRequest);

        AuthData joeAuth = authDAO.getAuthFromUser(registerRequest.username());
        CreateGameRequest createGameRequest = new CreateGameRequest("Joes Game", joeAuth.authToken());
        int gameID = service.createGame(createGameRequest).gameID();

        JoinGameRequest joeRequest = new JoinGameRequest("WHITE", gameID, joeAuth.authToken());
        Assertions.assertDoesNotThrow(() -> service.joinGame(joeRequest));

        RegisterRequest registerRequest2 = new RegisterRequest("Bob", "12346", "bob@bob.com");
        service.register(registerRequest2);
        AuthData bobAuth = authDAO.getAuthFromUser(registerRequest2.username());

        JoinGameRequest bobRequest = new JoinGameRequest("WHITE", gameID, bobAuth.authToken());
        Assertions.assertThrows(AlreadyTakenException.class, () -> service.joinGame(bobRequest));
    }

    @Test
    @DisplayName("Clear Success")
    public void clearSuccess(){
        service.clear();
        RegisterRequest registerRequest = new RegisterRequest("Joe", "12345", "joe@joe.com");
        service.register(registerRequest);

        AuthData joeAuth = authDAO.getAuthFromUser(registerRequest.username());
        CreateGameRequest createGameRequest = new CreateGameRequest("Joes Game", joeAuth.authToken());
        service.createGame(createGameRequest);
        Assertions.assertFalse(MemoryGameDAO.GAME_LIST.isEmpty());

        service.clear();

        Assertions.assertTrue(MemoryGameDAO.GAME_LIST.isEmpty());
    }
}

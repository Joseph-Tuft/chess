package client;

import dataaccess.exceptions.DataAccessException;
import model.requests.*;
import model.responses.*;
import org.junit.jupiter.api.*;
import server.Server;
import service.Service;


public class ServerFacadeTests {

    private static Server server;
    private final Service service = new Service();

    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade(String.format("http://localhost:%d", port));
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clear() {
        service.clear();
    }

    @Test
    @DisplayName("Register Success")
    public void registerSuccess(){
        RegisterRequest request = new RegisterRequest("Joe", "12345", "joe@joe.com");
        Assertions.assertDoesNotThrow(()->facade.register(request));
    }

    @Test
    @DisplayName("Register Fail")
    public void registerFail(){
        RegisterRequest request = new RegisterRequest("Joe", "12345", "joe@joe.com");
        facade.register(request);
        Assertions.assertThrows(DataAccessException.class, ()->facade.register(request));
    }

    @Test
    @DisplayName("Login Success")
    public void loginSuccess(){
        RegisterRequest registerRequest = new RegisterRequest("Joe", "12345", "joe@joe.com");
        String auth = facade.register(registerRequest).authToken();
        LogoutRequest logoutRequest = new LogoutRequest(auth);
        facade.logout(logoutRequest);
        LoginRequest loginRequest = new LoginRequest("Joe", "12345");
        Assertions.assertDoesNotThrow(()->facade.login(loginRequest));
    }

    @Test
    @DisplayName("Login Fail")
    public void loginFail(){
        RegisterRequest registerRequest = new RegisterRequest("Joe", "12345", "joe@joe.com");
        String auth = facade.register(registerRequest).authToken();
        LogoutRequest logoutRequest = new LogoutRequest(auth);
        facade.logout(logoutRequest);
        LoginRequest loginRequest = new LoginRequest("Joey", "12345");
        Assertions.assertThrows(DataAccessException.class, ()->facade.login(loginRequest));
    }

    @Test
    @DisplayName("Logout Success")
    public void logoutSuccess(){
        RegisterRequest registerRequest = new RegisterRequest("Joe", "12345", "joe@joe.com");
        String auth = facade.register(registerRequest).authToken();
        LogoutRequest logoutRequest = new LogoutRequest(auth);
        Assertions.assertDoesNotThrow(()->facade.logout(logoutRequest));
    }

    @Test
    @DisplayName("Logout Fail")
    public void logoutFail(){
        RegisterRequest registerRequest = new RegisterRequest("Joe", "12345", "joe@joe.com");
        facade.register(registerRequest);
        LogoutRequest logoutRequest = new LogoutRequest("12345");
        Assertions.assertThrows(DataAccessException.class, ()->facade.logout(logoutRequest));
    }

    @Test
    @DisplayName("Create Game Success")
    public void createGameSuccess() {
        RegisterRequest registerRequest = new RegisterRequest("Joe", "12345", "joe@joe.com");
        String auth = facade.register(registerRequest).authToken();
        CreateGameRequest gameRequest = new CreateGameRequest("Game1", auth);
        Assertions.assertDoesNotThrow(() -> facade.createGame(gameRequest));
    }

    @Test
    @DisplayName("Create Game Fail")
    public void createGameFail() {
        RegisterRequest registerRequest = new RegisterRequest("Joe", "12345", "joe@joe.com");
        String auth = facade.register(registerRequest).authToken();
        CreateGameRequest gameRequest = new CreateGameRequest("Game1", auth);
        facade.createGame(gameRequest);
        Assertions.assertThrows(DataAccessException.class, () -> facade.createGame(gameRequest));
    }

    @Test
    @DisplayName("List Game Success")
    public void listGameSuccess() {
        RegisterRequest registerRequest = new RegisterRequest("Joe", "12345", "joe@joe.com");
        String auth = facade.register(registerRequest).authToken();
        CreateGameRequest gameRequest1 = new CreateGameRequest("Game1", auth);
        CreateGameRequest gameRequest2 = new CreateGameRequest("Game2", auth);
        Assertions.assertDoesNotThrow(() -> facade.createGame(gameRequest1));
        Assertions.assertDoesNotThrow(() -> facade.createGame(gameRequest2));

        ListGamesRequest listGamesRequest = new ListGamesRequest(auth);
        ListGamesResponse response = facade.listGames(listGamesRequest);
        Assertions.assertEquals(2, response.games().size());
    }

    @Test
    @DisplayName("List Game Fail")
    public void listGameFail() {
        RegisterRequest registerRequest = new RegisterRequest("Joe", "12345", "joe@joe.com");
        String auth = facade.register(registerRequest).authToken();
        CreateGameRequest gameRequest1 = new CreateGameRequest("Game1", auth);
        CreateGameRequest gameRequest2 = new CreateGameRequest("Game2", auth);
        Assertions.assertDoesNotThrow(() -> facade.createGame(gameRequest1));
        Assertions.assertDoesNotThrow(() -> facade.createGame(gameRequest2));

        ListGamesRequest listGamesRequest = new ListGamesRequest("12345");
        Assertions.assertThrows(DataAccessException.class, ()->facade.listGames(listGamesRequest));
    }

    @Test
    @DisplayName("Join Game Success")
    public void joinGameSuccess() {
        RegisterRequest registerRequest = new RegisterRequest("Joe", "12345", "joe@joe.com");
        String auth = facade.register(registerRequest).authToken();
        CreateGameRequest gameRequest1 = new CreateGameRequest("Game1", auth);
        int id = facade.createGame(gameRequest1).gameID();
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", id, auth);
        Assertions.assertDoesNotThrow(() -> facade.joinGame(joinGameRequest));
    }

    @Test
    @DisplayName("Join Game Fail")
    public void joinGameFail() {
        RegisterRequest registerRequest = new RegisterRequest("Joe", "12345", "joe@joe.com");
        String auth = facade.register(registerRequest).authToken();
        CreateGameRequest gameRequest1 = new CreateGameRequest("Game1", auth);
        int id = facade.createGame(gameRequest1).gameID();
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHIT", id, auth);
        Assertions.assertThrows(DataAccessException.class, () -> facade.joinGame(joinGameRequest));
    }


}

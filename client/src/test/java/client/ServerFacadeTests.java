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
    private final ServerFacade facade = new ServerFacade("http://localhost:8080");

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
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
    @DisplayName("Register Fail")
    public void registerFailed(){
        RegisterRequest request = new RegisterRequest("Joe", "12345", "joe@joe.com");
        facade.register(request);
        Assertions.assertThrows(DataAccessException.class, ()->facade.register(request));
    }

}

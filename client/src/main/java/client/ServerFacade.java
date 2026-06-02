package client;

import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import model.requests.*;
import model.responses.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url){
        serverUrl = url;
    }

    public RegisterResponse register(RegisterRequest registerRequest) throws DataAccessException{
        var request = buildRequest("POST", "/user", registerRequest);
        var response = sendRequest(request);
        return handleResponse(response, RegisterResponse.class);
    }

    public LoginResponse login(LoginRequest loginRequest) throws DataAccessException{
        var request = buildRequest("POST", "/session", loginRequest);
        var response = sendRequest(request);
        return handleResponse(response, LoginResponse.class);
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException{
        var request = buildRequest("DELETE", "/session", logoutRequest);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public CreateGameResponse createGame(CreateGameRequest createGameRequest) throws DataAccessException{
        var request = buildRequest("POST", "/game", createGameRequest);
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResponse.class);
    }

    public ListGamesResponse listGames(ListGamesRequest listGamesRequest) throws DataAccessException{
        var request = buildRequest("GET", "/game", listGamesRequest);
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResponse.class);
    }

    public void joinGame(JoinGameRequest joinGameRequest) throws DataAccessException{
        var request = buildRequest("PUT", "/game", joinGameRequest);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public void clear() throws DataAccessException{
        var request = buildRequest("DELETE", "/db", null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public HttpRequest buildRequest(String method, String path, Object body){
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null){
            request.setHeader("authorization", "application/json");
        }
        return request.build();
    }

    public BodyPublisher makeRequestBody(Object request){
        if (request != null){
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    public HttpResponse<String> sendRequest(HttpRequest request) throws DataAccessException {
        try{
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e){
            throw new DataAccessException(String.format("Error: %s", e));
        }
    }

    public <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws DataAccessException {
        var status = response.statusCode();
        if(!isSuccessful(status)){
            var body = response.body();
            if (body!= null) {
                throw new DataAccessException(body);
            }
            throw new DataAccessException("From handle Response in Server Facade");
        }

        if (responseClass != null){
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    public boolean isSuccessful(int status){
        return status / 100 == 2;
    }
}

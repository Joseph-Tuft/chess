package server;

import com.google.gson.Gson;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.CustomErrorException;
import dataaccess.exceptions.UnauthorizedRequestException;
import io.javalin.*;
import io.javalin.http.Context;
import model.ErrorResponse;
import model.requests.*;
import service.Service;

import java.util.function.Supplier;

public class Server {
    private final Service service = new Service();

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::register);
        javalin.delete("/db", this::clear);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.post("/game", this::createGame);
        javalin.get("/game", this::listGames);
        javalin.put("/game", this::joinGame);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(Context ctx){
        RegisterRequest request = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        handlerHelper(ctx, () -> service.register(request));
    }

    private void clear(Context ctx){
        handlerHelper(ctx, service::clear);
    }

    private void login(Context ctx){
        LoginRequest request = new Gson().fromJson(ctx.body(), LoginRequest.class);
        handlerHelper(ctx, () -> service.login(request));
    }

    private void logout(Context ctx){
        LogoutRequest request = new LogoutRequest(ctx.header("authorization"));
        handlerHelper(ctx, () -> service.logout(request));
    }

    private void createGame(Context ctx){
        CreateGameRequest tempRequest = new Gson().fromJson(ctx.body(), CreateGameRequest.class);
        CreateGameRequest request = new CreateGameRequest(tempRequest.gameName(), ctx.header("authorization"));
        handlerHelper(ctx, () -> service.createGame(request));
    }

    private void listGames(Context ctx){
        ListGamesRequest request = new ListGamesRequest(ctx.header("authorization"));
        handlerHelper(ctx, () -> service.listGames(request));
    }

    private void joinGame(Context ctx){
        JoinGameRequest tempRequest = new Gson().fromJson(ctx.body(), JoinGameRequest.class);
        JoinGameRequest request = new JoinGameRequest(tempRequest.playerColor(), tempRequest.gameID(), ctx.header("authorization"));
        handlerHelper(ctx, () -> service.joinGame(request));
    }

    private void handlerHelper(Context ctx, Supplier<Object> serviceMethod){
        try{
            Object response = serviceMethod.get();
            ctx.result(new Gson().toJson(response));
            ctx.status(200);
        } catch (BadRequestException e) {
            ctx.result(new Gson().toJson(new ErrorResponse(e.getMessage())));
            ctx.status(400);
        } catch (UnauthorizedRequestException e) {
            ctx.result(new Gson().toJson(new ErrorResponse(e.getMessage())));
            ctx.status(401);
        } catch (AlreadyTakenException e){
            ctx.result(new Gson().toJson(new ErrorResponse(e.getMessage())));
            ctx.status(403);
        } catch (CustomErrorException e){
            ctx.result(new Gson().toJson(new ErrorResponse(e.getMessage())));
            ctx.status(500);
        }

    }
}
package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import service.Service;
import model.*;

import java.util.function.Supplier;

public class Server {
    private Service service = new Service();

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
        handlerHelper(ctx, request, () -> service.register(request));
    }

    private void clear(Context ctx){
        try{
            service.clear();
            ctx.result("{}");
            ctx.status(200);
        } catch (DataAccessException e){
            ctx.result(new Gson().toJson(new ErrorResponse(e.getMessage())));
            ctx.status(500);
        }
    }

    private void login(Context ctx){
        LoginRequest request = new Gson().fromJson(ctx.body(), LoginRequest.class);
        handlerHelper(ctx, request, () -> service.login(request));
    }

    private void logout(Context ctx){
        LogoutRequest request = new LogoutRequest(ctx.header("authorization"));
        handlerHelper(ctx, request, () -> service.logout(request));
    }

    private void createGame(Context ctx){
        CreateGameRequest tempRequest = new Gson().fromJson(ctx.body(), CreateGameRequest.class);
        CreateGameRequest request = new CreateGameRequest(tempRequest.gameName(), ctx.header("authorization"));
        handlerHelper(ctx, request, () -> service.createGame(request));
    }

    private void listGames(Context ctx){
        ListGamesRequest request = new ListGamesRequest(ctx.header("authorization"));
    }

    private void handlerHelper(Context ctx, Object request, Supplier<Object> serviceMethod){
        try{
            Object response = serviceMethod.get();
            ctx.result(new Gson().toJson(response));
            ctx.status(200);
        } catch (IncompleteRequestException e) {
            ctx.result(new Gson().toJson(new ErrorResponse(e.getMessage())));
            ctx.status(400);
        } catch (UnauthorizedRequestException e) {
            ctx.result(new Gson().toJson(new ErrorResponse(e.getMessage())));
            ctx.status(401);
        } catch (DuplicateUsernameException e){
            ctx.result(new Gson().toJson(new ErrorResponse(e.getMessage())));
            ctx.status(403);
        } catch (CustomErrorException e){
            ctx.result(new Gson().toJson(new ErrorResponse(e.getMessage())));
            ctx.status(500);
        }

    }
}
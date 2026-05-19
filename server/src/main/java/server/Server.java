package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import model.AuthData;
import model.RegisterRequest;
import model.RegisterResponse;
import model.UserData;
import service.Service;
import model.*;

public class Server {
    private Service service = new Service();

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::register);
        javalin.delete("/db", this::clear);

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

        try{
            RegisterResponse response = service.register(request);
            ctx.result(new Gson().toJson(response));
            ctx.status(200);
        } catch (DataAccessException e){
            ctx.result(new Gson().toJson(new ErrorResponse(e.getMessage())));
            ctx.status(403);
        }
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
}
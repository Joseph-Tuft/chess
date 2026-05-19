package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import service.Service;

public class Server {
    private Service service = new Service();

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::register);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(Context ctx){
        UserData user = new Gson().fromJson(ctx.body(), UserData.class);

        try{
            AuthData result = service.register(user);
            ctx.result(new Gson().toJson(result));
            ctx.status(200);
        } catch (DataAccessException e){
            ctx.result(new Gson().toJson(new ErrorResponse(e.getMessage())));
            ctx.status(403);
        }
    }
}
package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDao;
import dataaccess.memory.MemoryUserDAO;
import io.javalin.*;
import model.GsonUtil;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        Gson gson = GsonUtil.buildGson();

        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDao();

        UserService userService = new UserService(authDAO, userDAO);

        javalin.post("/user",
                new GenericHandler<>(
                        gson,
                        authDAO,
                        UserService.RegisterRequest.class,
                        UserService.RegisterResult.class,
                        (GenericHandler.PublicServiceEndpoint<UserService.RegisterRequest, UserService.RegisterResult>)
                                userService::register));

        javalin.post("/session",
                new GenericHandler<>(
                        gson,
                        authDAO,
                        UserService.LoginRequest.class,
                        UserService.LoginResult.class,
                        (GenericHandler.PublicServiceEndpoint<UserService.LoginRequest, UserService.LoginResult>)
                                userService::login));

        javalin.delete("/session",
                new GenericHandler<>(
                        gson,
                        authDAO,
                        Void.class,
                        Void.class,
                        (GenericHandler.LogoutServiceEndpoint) userService::logout
                ));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}

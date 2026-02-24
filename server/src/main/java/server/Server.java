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
import service.GameService;
import service.MiscService;
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
        GameService gameService = new GameService(gameDAO);
        MiscService miscService = new MiscService(authDAO, gameDAO, userDAO);

        javalin.post("/user",
                new GenericHandler<>(
                        gson,
                        authDAO,
                        UserService.RegisterRequest.class,
                        UserService.RegisterResult.class,
                        (GenericHandler.PublicServiceEndpoint<UserService.RegisterRequest, UserService.RegisterResult>)
                                userService::register,
                        false
                ));

        javalin.post("/session",
                new GenericHandler<>(
                        gson,
                        authDAO,
                        UserService.LoginRequest.class,
                        UserService.LoginResult.class,
                        (GenericHandler.PublicServiceEndpoint<UserService.LoginRequest, UserService.LoginResult>)
                                userService::login,
                        false
                ));

        javalin.delete("/session",
                new GenericHandler<>(
                        gson,
                        authDAO,
                        Void.class,
                        Void.class,
                        (GenericHandler.LogoutServiceEndpoint) userService::logout,
                        true
                ));

        javalin.get("/game",
                new GenericHandler<>(
                        gson,
                        authDAO,
                        Void.class,
                        GameService.ListResponse.class,
                        (GenericHandler.EmptyServiceEndpoint<GameService.ListResponse>) gameService::listGames,
                        true
                ));

        javalin.post("/game",
                new GenericHandler<>(
                        gson,
                        authDAO,
                        GameService.CreateRequest.class,
                        GameService.CreateResponse.class,
                        (GenericHandler.PublicServiceEndpoint<GameService.CreateRequest, GameService.CreateResponse>)
                                gameService::createGame,
                        true
                ));

        javalin.put("/game",
                new GenericHandler<>(
                        gson,
                        authDAO,
                        GameService.JoinRequest.class,
                        Void.class,
                        (GenericHandler.AuthVoidServiceEndpoint<GameService.JoinRequest>) gameService::joinGame,
                        true
                ));

        javalin.delete("/db",
                new GenericHandler<>(
                        gson,
                        authDAO,
                        Void.class,
                        Void.class,
                        (GenericHandler.BlankEndpoint) miscService::clearDatabase,
                        false
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

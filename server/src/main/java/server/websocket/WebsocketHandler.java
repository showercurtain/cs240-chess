package server.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import model.AuthData;
import model.GameData;
import service.ServiceException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class WebsocketHandler implements Consumer<WsConfig> {

    AuthDAO authDAO;
    GameDAO gameDAO;

    final Gson gson;
    public final ConcurrentHashMap<Integer, ActiveGame> games = new ConcurrentHashMap<>();

    public WebsocketHandler(Gson gson, AuthDAO authDAO, GameDAO gameDAO) {
        this.gson = gson;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    private AuthData checkAuth(String authToken, WsMessageContext ctx) {
        AuthData auth;
        try {
            auth = authDAO.getAuth(authToken);
            if (auth == null) {
                throw ServiceException.UNAUTHORIZED;
            }
        } catch (ServiceException e) {
            ctx.send(gson.toJson(new ServerMessage(e.getMessage(), true)));
            return null;
        }
        return auth;
    }

    private void handleConnect(WsConnectContext ctx) {
        ctx.enableAutomaticPings();
    }

    private void handleMessage(WsMessageContext ctx) {
        UserGameCommand command;
        System.out.println(ctx.message());
        try {
            command = gson.fromJson(ctx.message(), UserGameCommand.class);
        } catch (JsonSyntaxException e) {
            ctx.send(gson.toJson(new ServerMessage(e.getMessage(), true)));
            return;
        }
        AuthData auth = checkAuth(command.getAuthToken(), ctx);
        if (auth == null) {
            return;
        }
        AtomicReference<ServerMessage> res = new AtomicReference<>();
        games.compute(command.gameID(), (id, game) -> {
            GameData gameData;
            try {
                gameData = gameDAO.getGame(id);
            } catch (DataAccessException e) {
                res.set(new ServerMessage(e.getMessage(), true));
                return game;
            }
            if (gameData == null) {
                res.set(new ServerMessage("Programmer error: Active game has no database counterpart", true));
                return game;
            }
            if (game == null && command.commandType() == UserGameCommand.CommandType.CONNECT) {
                game = new ActiveGame(gson, gameDAO);
            } else if (game == null) {
                res.set(new ServerMessage("Game not active", true));
                return null;
            }

            return game.handleCommand(command, ctx.session, auth.username(), gameData);

        });

        if (res.get() != null) {
            ctx.send(gson.toJson(res.get()));
        }
    }

    private void handleClose(WsCloseContext ctx) {
    }

    @Override
    public void accept(WsConfig config) {
        config.onConnect(this::handleConnect);
        config.onMessage(this::handleMessage);
        config.onClose(this::handleClose);
    }
}

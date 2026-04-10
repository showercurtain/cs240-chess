package server.websocket;

import chess.ChessGame;
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
import org.eclipse.jetty.websocket.api.Session;
import service.ServiceException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class WebsocketHandler implements Consumer<WsConfig> {
    private static class SessionState {
        private enum SessionStateType {
            NotPlaying,
            InGame,
            Observing;
        }

        SessionStateType type = SessionStateType.NotPlaying;
        int gameID = -1;
        ChessGame.TeamColor team = null;
    }

    AuthDAO authDAO;
    GameDAO gameDAO;

    final Gson gson;
    final ConcurrentHashMap<Session, SessionState> connections = new ConcurrentHashMap<>();
    final ConcurrentHashMap<Integer, ActiveGame> games = new ConcurrentHashMap<>();

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
        connections.put(ctx.session, new SessionState());
    }

    private void handleMessage(WsMessageContext ctx) {
        UserGameCommand command;
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
        SessionState state = connections.remove(ctx.session);
        if (state == null || state.type == SessionState.SessionStateType.NotPlaying) {
            return;
        }

        games.computeIfPresent(state.gameID, (_id, game) -> {
            boolean remove = switch (state.team) {
                case WHITE -> game.unsetWhite();
                case BLACK -> game.unsetBlack();
                case null -> game.removeObserver(ctx.session);
            };
            return remove ? null : game;
        });
    }

    @Override
    public void accept(WsConfig config) {
        config.onConnect(this::handleConnect);
        config.onMessage(this::handleMessage);
        config.onClose(this::handleClose);
    }
}

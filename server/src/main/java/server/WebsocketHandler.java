package server;

import chess.ChessGame;
import com.google.gson.Gson;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import org.eclipse.jetty.websocket.api.Session;

import java.util.concurrent.ConcurrentHashMap;
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

    final Gson gson;
    final ConcurrentHashMap<Session, SessionState> connections = new ConcurrentHashMap<>();
    final ConcurrentHashMap<Integer, ActiveGame> games = new ConcurrentHashMap<>();

    public WebsocketHandler(Gson gson) {
        this.gson = gson;
    }

    private void handleConnect(WsConnectContext ctx) {
        ctx.enableAutomaticPings();
        connections.put(ctx.session, new SessionState());
    }

    private void handleMessage(WsMessageContext ctx) {

    }

    private void handleClose(WsCloseContext ctx) {
        SessionState state = connections.remove(ctx.session);
        if (state.type == SessionState.SessionStateType.NotPlaying) {
            return;
        }
        games.computeIfPresent(state.gameID, (_id, game) -> {
            boolean remove = state.team == ChessGame.TeamColor.WHITE ? game.unsetWhite() : game.unsetBlack();
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

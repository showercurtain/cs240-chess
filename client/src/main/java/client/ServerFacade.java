package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import model.AuthData;
import model.GameData;
import model.GsonUtil;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class ServerFacade {
    public record HttpError(String message) {}

    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = GsonUtil.buildRelaxedGson();
    private final URI serverHttpURI;
    private final URI serverWsURI;
    private Session session;

    public ServerFacade(String serverURI) throws URISyntaxException {
        this.serverHttpURI = new URI("http://"+serverURI);
        this.serverWsURI = new URI("ws://" + serverURI);
    }

    private <U, T> T makeRequest(String url, String method, AuthData auth, U request, Class<T> responseClass) throws ServerException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(serverHttpURI.resolve(url))
                .timeout(TIMEOUT);
        if (auth != null) {
            requestBuilder.header("authorization", auth.authToken());
        }
        if (request != null) {
            requestBuilder.header("Content-Type", "application/json");
            requestBuilder.method(method, HttpRequest.BodyPublishers.ofString(gson.toJson(request)));
        } else {
            requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
        }
        HttpRequest httpRequest = requestBuilder.build();
        try {
            HttpResponse<String> res = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() >= 400) {
                if (res.body() == null || res.body().isEmpty()) {
                    throw new ServerException("Unknown error", res.statusCode());
                } else {
                    throw new ServerException(gson.fromJson(res.body(), HttpError.class).message, res.statusCode());
                }
            }
            if (responseClass == null) { return null; }
            return gson.fromJson(res.body(), responseClass);
        } catch(IOException e) {
            throw new ServerException("IO error: " + e.getMessage(), 0);
        } catch (InterruptedException e) {
            throw new ServerException("Interrupted", 0);
        }
    }

    public AuthData register(String username, String password, String email) throws ServerException {
        return makeRequest("/user", "POST", null, Map.of("username", username, "password", password, "email", email), AuthData.class);
    }

    public AuthData login(String username, String password) throws ServerException {
        return makeRequest("/session", "POST", null, Map.of("username", username, "password", password), AuthData.class);
    }

    public void logout(AuthData auth) throws ServerException {
        makeRequest("/session", "DELETE", auth, null, null);
    }

    public void clear() throws ServerException {
        makeRequest("/db", "DELETE", null, null, null);
    }

    public record ListResponse(Collection<GameData> games){}
    public Collection<GameData> listGames(AuthData auth) throws ServerException {
        ListResponse res = makeRequest("/game", "GET", auth, null, ListResponse.class);
        if (res == null) {
            throw new ServerException("Programmer error", 0);
        }
        return res.games;
    }

    public record CreateResponse(int gameID) {}
    public int createGame(String gameName, AuthData auth) throws ServerException {
        CreateResponse res = makeRequest("/game", "POST", auth, Map.of("gameName", gameName), CreateResponse.class);
        if (res == null) {
            throw new ServerException("Programmer error", 0);
        }
        return res.gameID;
    }

    public void joinGame(AuthData auth, ChessGame.TeamColor playerColor, int gameID) throws ServerException {
        makeRequest("/game", "PUT", auth, Map.of("playerColor", playerColor, "gameID", gameID), null);
    }

    public void connectWebsocket(Consumer<ServerMessage> handler) throws ServerException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            session = container.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig endpointConfig) {
                    session.addMessageHandler(String.class, msg -> {
                        ServerMessage message = gson.fromJson(msg, ServerMessage.class);
                        handler.accept(message);
                    });
                }
            }, serverWsURI.resolve("/ws"));
        } catch (IOException e) {
            throw new ServerException(e.getMessage(),400);
        } catch (DeploymentException e) {
            throw new ServerException(e.getMessage(),500);
        }
    }

    public void closeWebsocket() throws ServerException {
        try {
            session.close();
            session = null;
        } catch (IOException e) {
            throw new ServerException(e.getMessage(),400);
        }
    }

    public void wsCommand(UserGameCommand.CommandType command, AuthData auth, int gameID, ChessMove move) throws ServerException {
        if (session != null && session.isOpen()) {
            UserGameCommand message = new UserGameCommand(
                    command, auth.authToken(), gameID, Optional.ofNullable(move)
            );
            try {
                session.getBasicRemote().sendText(gson.toJson(message));
            } catch (IOException e) {
                throw new ServerException(e.getMessage(), 400);
            }
        }
    }
}

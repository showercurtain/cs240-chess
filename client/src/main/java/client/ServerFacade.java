package client;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.GsonUtil;

import java.net.http.HttpClient;
import java.util.Collection;

public class ServerFacade {
    private static final int TIMEOUT_MILLIS = 5000;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = GsonUtil.buildGson();

    public AuthData register(String username, String password, String email) {
        throw new RuntimeException("Not implemented");
    }

    public AuthData login(String username, String password) {
        throw new RuntimeException("Not implemented");
    }

    public void logout(AuthData auth) {
        throw new RuntimeException("Not implemented");
    }

    public Collection<GameData> listGames() {
        throw new RuntimeException("Not implemented");
    }

    public int createGame(String gameName) {
        throw new RuntimeException("Not implemented");
    }

    public void joinGame(AuthData auth, ChessGame.TeamColor playerColor, int gameID) {
        throw new RuntimeException("Not implemented");
    }
}

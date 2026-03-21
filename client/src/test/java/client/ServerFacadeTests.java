package client;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static AuthData auth;
    private static AuthData auth2;
    private static int gameID;

    @BeforeAll
    public static void init() throws URISyntaxException, ServerException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(new URI("http://localhost:"+port));
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Order(1)
    @Test
    public void registerTest() throws ServerException {
        auth = facade.register("user", "password", "email@example.com");
        assert auth.username().equals("user");
    }

    @Test
    @Order(2)
    public void usernameTakenTest() {
        ServerException error = null;
        try {
            facade.register("user","qwertyuiop", "email@example.com");
        } catch (ServerException e) {
            error = e;
        }
        assert error != null;
        assert error.getHttpCode() == 403;
    }

    @Test
    @Order(3)
    public void loginTest() throws ServerException {
        auth2 = facade.login("user","password");
        assert auth2.username().equals(auth.username());
        assert !auth2.authToken().equals(auth.authToken());
    }

    @Test
    @Order(4)
    public void logoutTest() throws ServerException {
        facade.logout(auth2);
        // I don't know how to test a logout failure with how I've set it up
        assert true;
    }

    @Test
    @Order(5)
    public void loginNoUserTest() {
        ServerException error = null;
        try {
            facade.login("nonexistent","password");
        } catch (ServerException e) {
            error = e;
        }
        assert error != null;
        assert error.getHttpCode() == 401;
    }

    @Test
    @Order(6)
    public void loginIncorrectPasswordTest() {
        ServerException error = null;
        try {
            facade.login("user","incorrect");
            // The password is "incorrect"
            // Maybe I should try "again"?
        } catch (ServerException e) {
            error = e;
        }
        assert error != null;
        assert error.getHttpCode() == 401;
    }

    @Test
    @Order(7)
    public void initializeEmptyTest() throws ServerException {
        Collection<GameData> games = facade.listGames(auth);
        assert games.isEmpty();
    }

    @Test
    @Order(8)
    public void createGameTest() throws ServerException {
        gameID = facade.createGame("Game", auth);
        assert gameID > 0;
    }

    @Test
    @Order(9)
    public void oneGameTest() throws ServerException {
        Collection<GameData> games = facade.listGames(auth);
        assert games.size() == 1;
    }

    @Test
    @Order(10)
    public void createMoreGamesTest() throws ServerException {
        int id1 = facade.createGame("Game", auth);
        assert id1 > 0;
        int id2 = facade.createGame("Game", auth);
        assert id2 > 0;
        assert id1 != id2;

        Collection<GameData> games = facade.listGames(auth);
        assert games.size() == 3;
    }

    @Test
    @Order(11)
    public void joinNonexistentGameTest() {
        ServerException error = null;
        try {
            facade.joinGame(auth, ChessGame.TeamColor.WHITE, -5);
        } catch (ServerException e) {
            error = e;
        }

        assert error != null;
        assert error.getHttpCode() == 400;
    }

    @Test
    @Order(12)
    public void joinGameTest() throws ServerException {
        facade.joinGame(auth, ChessGame.TeamColor.WHITE, gameID);
        Collection<GameData> games = facade.listGames(auth);

        assert games.size() == 3;

        Optional<GameData> game = games.stream().filter(data -> data.gameID() == gameID).findFirst();

        assert game.isPresent();
        assert game.get().whiteUsername().equals(auth.username());
    }

    @Test
    @Order(13)
    public void joinGameAlreadyTakenTest() {

        ServerException error = null;
        try {
            facade.joinGame(auth, ChessGame.TeamColor.WHITE, gameID);
        } catch (ServerException e) {
            error = e;
        }

        assert error != null;
        assert error.getHttpCode() == 403;
    }
}

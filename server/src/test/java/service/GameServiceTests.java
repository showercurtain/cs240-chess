package service;

import chess.ChessGame;
import dataaccess.memory.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;

import java.util.Collection;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameServiceTests {
    private static GameService service;
    private static final AuthData AUTH = new AuthData("token","testing");
    private static int gameID = 0;

    @BeforeAll
    public static void init() {
        service = new GameService(new MemoryGameDAO());
    }

    @Test
    @Order(1)
    public void initializeEmptyTest() throws ServiceException {
        Collection<GameData> games = service.listGames().games();
        assert games.isEmpty();
    }

    @Test
    @Order(2)
    public void createGameTest() throws ServiceException {
        GameService.CreateResponse response = service.createGame(new GameService.CreateRequest("Game"));
        assert response.gameID() > 0;
        gameID = response.gameID();
    }

    @Test
    @Order(3)
    public void oneGameTest() throws ServiceException {
        Collection<GameData> games = service.listGames().games();
        assert games.size() == 1;
    }

    @Test
    @Order(4)
    public void createMoreGamesTest() throws ServiceException {
        GameService.CreateResponse response1 = service.createGame(new GameService.CreateRequest("Game"));
        assert response1.gameID() > 0;
        GameService.CreateResponse response2 = service.createGame(new GameService.CreateRequest("Game"));
        assert response2.gameID() > 0;
        assert response1.gameID() != response2.gameID();

        Collection<GameData> games = service.listGames().games();
        assert games.size() == 3;
    }

    @Test
    @Order(5)
    public void joinNonexistentGameTest() {
        ServiceException error = null;
        try {
            service.joinGame(AUTH, new GameService.JoinRequest(ChessGame.TeamColor.WHITE, 0));
        } catch (ServiceException e) {
            error = e;
        }

        assert error != null;
        assert error.getHttpError() == 400;
    }

    @Test
    @Order(6)
    public void joinGameTest() throws ServiceException {
        service.joinGame(AUTH, new GameService.JoinRequest(ChessGame.TeamColor.WHITE, gameID));
        Collection<GameData> games = service.listGames().games();

        assert games.size() == 3;

        Optional<GameData> game = games.stream().filter(data -> data.gameID() == gameID).findFirst();

        assert game.isPresent();
        assert game.get().whiteUsername().equals(AUTH.username());
    }

    @Test
    @Order(7)
    public void joinGameAlreadyTakenTest() {
        ServiceException error = null;
        try {
            service.joinGame(AUTH, new GameService.JoinRequest(ChessGame.TeamColor.WHITE, gameID));
        } catch (ServiceException e) {
            error = e;
        }

        assert error != null;
        assert error.getHttpError() == 403;
    }
}

package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.mysql.MySQLGameDAO;
import model.GameData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class GameDAOTests {
    public static final MySQLGameDAO MYSQL_GAME_DAO = new MySQLGameDAO();

    @BeforeAll
    public static void init() throws DataAccessException {
        MYSQL_GAME_DAO.initTable();
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        GameDAOTests.cleanup();
    }

    @AfterAll
    public static void cleanup() throws DataAccessException {
        MYSQL_GAME_DAO.clear();
    }

    @Test
    public void createGameTest() throws DataAccessException {
        ChessGame game = new ChessGame();
        int gameID = MYSQL_GAME_DAO.createGame(new GameData(0, null, null, "game", game));

        GameData fetchedGame = MYSQL_GAME_DAO.getGame(gameID);
        assert fetchedGame != null;
        assert "game".equals(fetchedGame.gameName());
        assert game.equals(fetchedGame.game());
    }

    @Test
    public void createMultipleGameTest() throws DataAccessException {
        ChessGame game = new ChessGame();
        int gameID = MYSQL_GAME_DAO.createGame(new GameData(0, null, null, "game", game));
        int gameID2 = MYSQL_GAME_DAO.createGame(new GameData(0, null, null, "game2", game));
        assert gameID != gameID2;

        GameData fetchedGame = MYSQL_GAME_DAO.getGame(gameID);
        assert fetchedGame != null;
        assert "game".equals(fetchedGame.gameName());

        fetchedGame = MYSQL_GAME_DAO.getGame(gameID2);
        assert fetchedGame != null;
        assert "game2".equals(fetchedGame.gameName());

        fetchedGame = MYSQL_GAME_DAO.getGame(gameID + gameID2);
        assert fetchedGame == null;
    }

    @Test
    public void updateGameTest() throws DataAccessException {
        ChessGame game = new ChessGame();
        int gameID = MYSQL_GAME_DAO.createGame(new GameData(0, null, null, "game", game));

        MYSQL_GAME_DAO.updateGame(new GameData(gameID, null, "QATester5000", "game", game));
        GameData fetchedGame = MYSQL_GAME_DAO.getGame(gameID);
        assert fetchedGame != null;
        assert "QATester5000".equals(fetchedGame.blackUsername());

        try {
            game.makeMove(new ChessMove(
                    new ChessPosition(2,5),
                    new ChessPosition(4, 5),
                    null
            ));
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }

        MYSQL_GAME_DAO.updateGame(new GameData(gameID, null, "QATester5000", "game", game));

        fetchedGame = MYSQL_GAME_DAO.getGame(gameID);
        assert fetchedGame != null;
        assert game.equals(fetchedGame.game());
    }

    @Test
    public void updateGameFailTest() throws DataAccessException {
        ChessGame game = new ChessGame();
        int gameID = MYSQL_GAME_DAO.createGame(new GameData(0, null, null, "game", game));

        boolean success = true;
        try {
            MYSQL_GAME_DAO.updateGame(new GameData(gameID+3, null, "QATester5000", "game", game));
        } catch (DataAccessException e) {
            success = false;
        }

        assert !success;
    }

    @Test
    public void listGamesTest() throws DataAccessException {
        Collection<GameData> games = MYSQL_GAME_DAO.listGames();
        assert games.isEmpty();

        MYSQL_GAME_DAO.createGame(new GameData(0, null, null, "newGame1", new ChessGame()));
        games = MYSQL_GAME_DAO.listGames();
        assert games.size() == 1;
        GameData data = (GameData) games.toArray()[0];
        assert "newGame1".equals(data.gameName());
    }
}

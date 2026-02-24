package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.Collection;

public class GameService {
    public record ListResponse(Collection<GameData> games) {}
    public record CreateRequest(String gameName) {}
    public record CreateResponse(int gameID) {}

    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public ListResponse listGames() throws ServiceException {
        return new ListResponse(gameDAO.listGames());
    }

    public CreateResponse createGame(CreateRequest request) throws ServiceException {
        int nextID = gameDAO.nextID();
        GameData game = new GameData(nextID, null, null, request.gameName(), new ChessGame());
        gameDAO.createGame(game);
        return new CreateResponse(nextID);
    }
}

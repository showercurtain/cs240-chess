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
    public record JoinRequest(ChessGame.TeamColor playerColor, int gameID) {}

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

    public void joinGame(AuthData auth, JoinRequest request) throws ServiceException {
        GameData game = gameDAO.getGame(request.gameID());
        if (game == null) throw new ServiceException("No such game").withError(400);
        if (request.playerColor == ChessGame.TeamColor.WHITE) {
            if (game.whiteUsername() != null) throw new ServiceException("already taken").withError(403);
            gameDAO.updateGame(new GameData(
                    game.gameID(),
                    auth.username(),
                    game.blackUsername(),
                    game.gameName(),
                    game.game()));
        } else {
            if (game.blackUsername() != null) throw new ServiceException("already taken").withError(403);
            gameDAO.updateGame(new GameData(
                    game.gameID(),
                    game.whiteUsername(),
                    auth.username(),
                    game.gameName(),
                    game.game()));
        }
    }
}

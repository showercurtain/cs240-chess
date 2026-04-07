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
        GameData game = new GameData(0, null, null, request.gameName(), new ChessGame());
        int id = gameDAO.createGame(game);
        return new CreateResponse(id);
    }

    public void joinGame(AuthData auth, JoinRequest request) throws ServiceException {
        GameData game = gameDAO.getGame(request.gameID());
        if (game == null) {
            throw ServiceException.NO_SUCH_GAME;
        }
        if (request.playerColor == ChessGame.TeamColor.WHITE) {
            if (game.whiteUsername() != null) {
                throw ServiceException.ALREADY_TAKEN;
            }
            gameDAO.updateGame(new GameData(
                    game.gameID(),
                    auth.username(),
                    game.blackUsername(),
                    game.gameName(),
                    game.game()));
        } else {
            if (game.blackUsername() != null) {
                throw ServiceException.ALREADY_TAKEN;
            }
            gameDAO.updateGame(new GameData(
                    game.gameID(),
                    game.whiteUsername(),
                    auth.username(),
                    game.gameName(),
                    game.game()));
        }
    }
}

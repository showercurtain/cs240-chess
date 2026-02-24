package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;

import java.util.Collection;

public class GameService {
    public record ListResponse(Collection<GameData> games) {}

    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public ListResponse listGames(AuthData ignored_auth) throws ServiceException {
        return new ListResponse(gameDAO.listGames());
    }
}

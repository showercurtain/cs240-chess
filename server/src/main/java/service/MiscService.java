package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import server.websocket.ActiveGame;

import java.util.concurrent.ConcurrentHashMap;

public class MiscService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;
    private final ConcurrentHashMap<Integer, ActiveGame> games;

    public MiscService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO, ConcurrentHashMap<Integer, ActiveGame> games) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
        this.games = games;
    }

    public void clearDatabase() throws ServiceException {
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
        games.clear();
    }
}

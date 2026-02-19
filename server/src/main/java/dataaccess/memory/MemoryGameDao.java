package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.DataNotFound;
import dataaccess.GameDAO;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDao implements GameDAO {
    HashMap<Integer, GameData> data;

    public MemoryGameDao() {
        data = new HashMap<>();
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        data.put(game.gameID(), game);
    }

    @Override
    public GameData getGame(int identifier) throws DataAccessException {
        GameData out = data.get(identifier);
        if (out == null) throw new DataNotFound("Game", identifier);
        return out.copy();
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return data.values().stream().map(GameData::copy).toList();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!data.containsKey(game.gameID())) throw new DataNotFound("Game", game.gameID());
        data.put(game.gameID(), game.copy());
    }

    @Override
    public void deleteGame(int identifier) throws DataAccessException {
        if (!data.containsKey(identifier)) throw new DataNotFound("Game", identifier);
        data.remove(identifier);
    }
}

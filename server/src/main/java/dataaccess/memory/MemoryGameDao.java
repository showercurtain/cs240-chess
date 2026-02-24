package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDao implements GameDAO {
    HashMap<Integer, GameData> data;

    public MemoryGameDao() {
        data = new HashMap<>();
    }

    @Override
    public void createGame(GameData game) {
        data.put(game.gameID(), game);
    }

    @Override
    public @Nullable GameData getGame(int identifier) {
        return data.get(identifier);
    }

    @Override
    public Collection<GameData> listGames() {
        return data.values().stream().map(GameData::copy).toList();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!data.containsKey(game.gameID())) throw new DataAccessException("Game " + game.gameID() + " not found");
        data.put(game.gameID(), game.copy());
    }

    @Override
    public void deleteGame(int identifier) {
        data.remove(identifier);
    }
}

package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryGameDao implements GameDAO {
    final HashMap<Integer, GameData> data;
    final AtomicInteger currentID;

    public MemoryGameDao() {
        data = new HashMap<>();
        currentID = new AtomicInteger(0);
    }

    @Override
    public void createGame(GameData game) {
        synchronized (data) { data.put(game.gameID(), game); }
    }

    @Override
    public @Nullable GameData getGame(int identifier) {
        synchronized (data) { return data.get(identifier); }
    }

    @Override
    public Collection<GameData> listGames() {
        synchronized (data) { return data.values().stream().map(GameData::copy).toList(); }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        synchronized (data) {
            if (!data.containsKey(game.gameID())) throw new DataAccessException("Game " + game.gameID() + " not found");
            data.put(game.gameID(), game.copy());
        }
    }

    @Override
    public int nextID() {
        return currentID.getAndIncrement();
    }

    @Override
    public void clear() {
        synchronized (data) {
            data.clear();
        }
    }
}

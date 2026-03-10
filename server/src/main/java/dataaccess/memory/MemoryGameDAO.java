package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryGameDAO implements GameDAO {
    final HashMap<Integer, GameData> data;
    final AtomicInteger currentID;

    public MemoryGameDAO() {
        data = new HashMap<>();
        currentID = new AtomicInteger(1);
    }

    @Override
    public int createGame(GameData game) {
        int id = nextID();
        GameData newGame = new GameData(id, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        synchronized (data) {
            data.put(id, newGame);
        }
        return id;
    }

    @Override
    public @Nullable GameData getGame(int identifier) {
        synchronized (data) {
            return data.get(identifier);
        }
    }

    @Override
    public Collection<GameData> listGames() {
        synchronized (data) {
            return data.values().stream().map(GameData::copy).toList();
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        synchronized (data) {
            if (!data.containsKey(game.gameID())) {
                throw new DataAccessException("Game " + game.gameID() + " not found");
            }
            data.put(game.gameID(), game.copy());
        }
    }

    private int nextID() {
        return currentID.getAndIncrement();
    }

    @Override
    public void clear() {
        synchronized (data) {
            data.clear();
        }
    }

    @Override
    public void initTable() { }
}

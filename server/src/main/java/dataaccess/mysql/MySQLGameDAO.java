package dataaccess.mysql;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MySQLGameDAO implements GameDAO {
    public MySQLGameDAO() {
    }

    @Override
    public int createGame(GameData game) {
    }

    @Override
    public @Nullable GameData getGame(int identifier) {
    }

    @Override
    public Collection<GameData> listGames() {
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
    }

    @Override
    public void clear() {
    }
}

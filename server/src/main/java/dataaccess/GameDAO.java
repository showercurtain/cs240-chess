package dataaccess;

import model.GameData;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface GameDAO {
    int createGame(GameData game) throws DataAccessException;
    @Nullable
    GameData getGame(int identifier) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
    void clear() throws DataAccessException;
    void initTable() throws DataAccessException;
}

package dataaccess;

import model.GameData;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface GameDAO {
    void createGame(GameData game) throws DataAccessException;
    @Nullable
    GameData getGame(int identifier) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
    void deleteGame(int identifier) throws DataAccessException;
    int nextID() throws DataAccessException;
}

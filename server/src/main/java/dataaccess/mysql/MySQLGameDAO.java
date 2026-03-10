package dataaccess.mysql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MySQLGameDAO implements GameDAO {
    public MySQLGameDAO() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public int createGame(GameData game) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public @Nullable GameData getGame(int identifier) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public Collection<GameData> listGames() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void clear() {
        throw new RuntimeException("Not Implemented");
    }

    public void initTable() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("""
CREATE TABLE IF NOT EXISTS games (
    id INT AUTO_INCREMENT PRIMARY KEY,
    whiteUsername VARCHAR(255),
    blackUsername VARCHAR(255),
    gameName VARCHAR(255) NOT NULL,
    game VARCHAR(2048) NOT NULL
)
""")) {
                ps.execute();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}

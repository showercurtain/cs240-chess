package dataaccess.mysql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;
import model.GsonUtil;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class MySQLGameDAO implements GameDAO {
    private final Gson GSON;

    public MySQLGameDAO() {
        GSON = GsonUtil.buildGson();
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "INSERT INTO games (gameName, game) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                String gameJSON = GSON.toJson(game.game());
                ps.setString(1, game.gameName());
                ps.setString(2, gameJSON);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new DataAccessException("Error creating game");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public @Nullable GameData getGame(int identifier) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT whiteUsername, blackUsername, gameName, game FROM games WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, identifier);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs == null || !rs.next()) {
                        return null;
                    }
                    return new GameData(
                            identifier,
                            rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            GSON.fromJson(rs.getString(4), ChessGame.class));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT id, whiteUsername, blackUsername, gameName, game FROM games";
            try (ResultSet rs = conn.prepareStatement(query).executeQuery()) {
                if (rs == null) {
                    return null;
                }
                ArrayList<GameData> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(new GameData(
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            GSON.fromJson(rs.getString(5), ChessGame.class)));
                }
                return out;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ?  WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(5, game.gameID());
                String gameJSON = GSON.toJson(game.game());
                ps.setString(1, game.whiteUsername());
                ps.setString(2, game.blackUsername());
                ps.setString(3, game.gameName());
                ps.setString(4, gameJSON);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (Statement statement = conn.createStatement()) {
                statement.addBatch("DROP TABLE games");
                statement.addBatch("""
CREATE TABLE games (
    id INT AUTO_INCREMENT PRIMARY KEY,
    whiteUsername VARCHAR(255),
    blackUsername VARCHAR(255),
    gameName VARCHAR(255) NOT NULL,
    game VARCHAR(2048) NOT NULL
)
""");
                statement.addBatch("ALTER TABLE games AUTO_INCREMENT=1");
                statement.executeBatch();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public void initTable() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            try (Statement statement = conn.createStatement()) {
                statement.addBatch("""
CREATE TABLE IF NOT EXISTS games (
    id INT AUTO_INCREMENT PRIMARY KEY,
    whiteUsername VARCHAR(255),
    blackUsername VARCHAR(255),
    gameName VARCHAR(255) NOT NULL,
    game VARCHAR(2048) NOT NULL
)
""");
                statement.addBatch("ALTER TABLE games AUTO_INCREMENT=1");
                statement.executeBatch();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}

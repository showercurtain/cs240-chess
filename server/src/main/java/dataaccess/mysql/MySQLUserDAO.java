package dataaccess.mysql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.UserData;
import org.jetbrains.annotations.Nullable;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class MySQLUserDAO implements UserDAO {
    @Override
    public void createUser(UserData user) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, user.username());
                ps.setString(2, BCrypt.hashpw(user.password(), BCrypt.gensalt()));
                ps.setString(3, user.email());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public @Nullable UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT email FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs == null || !rs.next()) {
                        return null;
                    }
                    return new UserData(username,null,rs.getString(1));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (Statement statement = conn.createStatement()) {
                statement.addBatch("DROP TABLE users");
                statement.addBatch("""
CREATE TABLE users (
    username VARCHAR(255) UNIQUE NOT NULL PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
)
""");
                statement.executeBatch();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public void initTable() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("""
CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(255) UNIQUE NOT NULL PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
)
""")) {
                ps.execute();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Nullable
    @Override
    public UserData getUserAuth(String username, String password) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT password, email FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs == null || !rs.next() || !BCrypt.checkpw(password, rs.getString(1))) {
                        return null;
                    }
                    return new UserData(username,null,rs.getString(2));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}

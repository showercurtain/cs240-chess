package dataaccess.mysql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.UserData;
import org.jetbrains.annotations.Nullable;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

public class MySQLUserDAO implements UserDAO {
    public MySQLUserDAO() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void createUser(UserData user) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public @Nullable UserData getUser(String username) {
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
}

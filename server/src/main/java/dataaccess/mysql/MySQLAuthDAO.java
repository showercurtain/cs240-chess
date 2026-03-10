package dataaccess.mysql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class MySQLAuthDAO implements AuthDAO {
    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "INSERT INTO auth (username, authToken) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, auth.username());
                ps.setString(2, auth.authToken());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public @Nullable AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT username, authToken FROM auth WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.getResultSet()) {
                    if (!rs.next()) {
                        return null;
                    }
                    return new AuthData(rs.getString(2),rs.getString(1));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void deleteAuth(String authToken) {
    }

    @Override
    public void clear() {
    }

    public void initTable() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("""
CREATE TABLE IF NOT EXISTS auth (
    'username' varchar(256) NOT NULL,
    'authToken' char(36) NOT NULL UNIQUE,
    PRIMARY KEY ('authToken')
)
""")) {
                ps.execute();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}

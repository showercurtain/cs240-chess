package dataaccess;

import dataaccess.mysql.MySQLAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class AuthDAOTests {
    private static final AuthDAO MYSQL_AUTH_DAO = new MySQLAuthDAO();

    @BeforeAll
    public static void init() throws DataAccessException {
        MYSQL_AUTH_DAO.initTable();
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        AuthDAOTests.cleanup();
    }

    @AfterAll
    public static void cleanup() throws DataAccessException {
        MYSQL_AUTH_DAO.clear();
    }

    @Test
    public void createAuthTest() throws DataAccessException {
        String token = UUID.randomUUID().toString();
        MYSQL_AUTH_DAO.createAuth(new AuthData(token, "testing"));

        AuthData auth = MYSQL_AUTH_DAO.getAuth(token);
        assert auth != null;
        assert "testing".equals(auth.username());
    }

    @Test
    public void incorrectAuthTest() throws DataAccessException {
        String token = UUID.randomUUID().toString();
        MYSQL_AUTH_DAO.createAuth(new AuthData(token, "testing"));

        String token2 = UUID.randomUUID().toString();

        AuthData auth = MYSQL_AUTH_DAO.getAuth(token2);
        assert auth == null;
    }

    @Test
    public void multipleLoginTest() throws DataAccessException {
        String token = UUID.randomUUID().toString();
        MYSQL_AUTH_DAO.createAuth(new AuthData(token, "testing"));

        AuthData auth = MYSQL_AUTH_DAO.getAuth(token);
        assert auth != null;

        String token2 = UUID.randomUUID().toString();
        MYSQL_AUTH_DAO.createAuth(new AuthData(token2, "testing"));

        auth = MYSQL_AUTH_DAO.getAuth(token2);
        assert auth != null;

        auth = MYSQL_AUTH_DAO.getAuth(token);
        assert auth != null;
    }

    @Test
    public void logoutTest() throws DataAccessException {
        String token = UUID.randomUUID().toString();
        MYSQL_AUTH_DAO.createAuth(new AuthData(token, "testing"));

        AuthData auth = MYSQL_AUTH_DAO.getAuth(token);
        assert auth != null;

        MYSQL_AUTH_DAO.deleteAuth(token);

        auth = MYSQL_AUTH_DAO.getAuth(token);
        assert auth == null;
    }
}

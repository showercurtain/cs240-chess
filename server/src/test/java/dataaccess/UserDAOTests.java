package dataaccess;

import dataaccess.mysql.MySQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.*;

public class UserDAOTests {
    private static final UserDAO MYSQL_USER_DAO = new MySQLUserDAO();

    @BeforeAll
    public static void init() throws DataAccessException {
        MYSQL_USER_DAO.initTable();
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        UserDAOTests.cleanup();
    }

    @AfterAll
    public static void cleanup() throws DataAccessException {
        MYSQL_USER_DAO.clear();
    }

    @Test
    public void createUser() throws DataAccessException {
        MYSQL_USER_DAO.createUser(new UserData("yeet","password","qwertyuiop@asdfghjkl.xyz"));
        UserData user = MYSQL_USER_DAO.getUser("yeet");
        assert user != null;
        assert "qwertyuiop@asdfghjkl.xyz".equals(user.email());
    }

    @Test
    public void createSameUser() throws DataAccessException {
        MYSQL_USER_DAO.createUser(new UserData("yeet","password","qwertyuiop@asdfghjkl.xyz"));
        boolean success = true;
        try {
            MYSQL_USER_DAO.createUser(new UserData("yeet","gabskjdetk","a@asdfghjkl.xyz"));
        } catch (DataAccessException e) {
            success = false;
        }
        assert !success;
        UserData user = MYSQL_USER_DAO.getUser("yeet");
        assert user != null;
        assert "qwertyuiop@asdfghjkl.xyz".equals(user.email());
    }

    @Test
    public void createMultipleUsers() throws DataAccessException {
        MYSQL_USER_DAO.createUser(new UserData("yeet","password","qwertyuiop@asdfghjkl.xyz"));
        MYSQL_USER_DAO.createUser(new UserData("yoink","password","giasfelfebrehber@asdfghjkl.xyz"));

        UserData user1 = MYSQL_USER_DAO.getUser("yeet");
        UserData user2 = MYSQL_USER_DAO.getUser("yoink");

        assert user1 != null;
        assert user2 != null;

        assert "qwertyuiop@asdfghjkl.xyz".equals(user1.email());
        assert "giasfelfebrehber@asdfghjkl.xyz".equals(user2.email());
    }

    @Test
    public void passwordAuthTest() throws DataAccessException {
        MYSQL_USER_DAO.createUser(new UserData("yeet","abc123","qwertyuiop@asdfghjkl.xyz"));

        UserData user = MYSQL_USER_DAO.getUserAuth("yeet","abc123");
        assert user != null;
        assert "qwertyuiop@asdfghjkl.xyz".equals(user.email());
    }

    @Test
    public void incorrectPasswordTest() throws DataAccessException {
        MYSQL_USER_DAO.createUser(new UserData("yoink","m1N3cr@f7","giasfelfebrehber@asdfghjkl.xyz"));

        UserData user = MYSQL_USER_DAO.getUserAuth("yeet","abc123");
        assert user == null;
        user = MYSQL_USER_DAO.getUserAuth("yoink","m1nc3r@f7");
        assert user == null;
    }
}

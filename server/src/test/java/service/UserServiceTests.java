package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryUserDAO;
import dataaccess.mysql.MySQLAuthDAO;
import dataaccess.mysql.MySQLUserDAO;
import model.AuthData;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTests {
    private static UserService service;
    private static AuthData auth;
    private static AuthData auth2;

    @BeforeAll
    public static void init() {
        AuthDAO authDAO = new MySQLAuthDAO();
        UserDAO userDAO = new MySQLUserDAO();
        try {
            authDAO.initTable();
            userDAO.initTable();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        service = new UserService(authDAO, userDAO);
    }

    @Order(1)
    @Test
    public void registerTest() throws ServiceException {
        auth = service.register(
                new UserService.RegisterRequest("user", "password", "email@example.com"));
        assert auth.username().equals("user");
    }

    @Test
    @Order(2)
    public void usernameTakenTest() {
        ServiceException error = null;
        try {
            service.register(
                    new UserService.RegisterRequest("user","qwertyuiop", "email@example.com"));
        } catch (ServiceException e) {
            error = e;
        }
        assert error != null;
        assert error.getHttpError() == 403;
    }

    @Test
    @Order(3)
    public void loginTest() throws ServiceException {
        auth2 = service.login(new UserService.LoginRequest("user","password"));
        assert auth2.username().equals(auth.username());
        assert !auth2.authToken().equals(auth.authToken());
    }

    @Test
    @Order(4)
    public void logoutTest() throws ServiceException {
        service.logout(auth2);
        // I don't know how to test a logout failure with how I've set it up
        assert true;
    }

    @Test
    @Order(5)
    public void loginNoUserTest() {
        ServiceException error = null;
        try {
            service.login(new UserService.LoginRequest("nonexistent","password"));
        } catch (ServiceException e) {
            error = e;
        }
        assert error != null;
        assert error.getHttpError() == 401;
    }

    @Test
    @Order(6)
    public void loginIncorrectPasswordTest() {
        ServiceException error = null;
        try {
            service.login(new UserService.LoginRequest("user","incorrect"));
            // The password is "incorrect"
            // Maybe I should try "again"?
        } catch (ServiceException e) {
            error = e;
        }
        assert error != null;
        assert error.getHttpError() == 401;
    }
}

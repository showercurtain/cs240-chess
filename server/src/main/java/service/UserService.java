package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    public record RegisterRequest(String username, String password, String email) {}
    public record RegisterResult(String username, String authToken) {}
    public record LoginRequest(String username, String password) {}
    public record LoginResult(String username, String authToken) {}

    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegisterResult register(RegisterRequest request) throws ServiceException {
        if (userDAO.getUser(request.username()) != null) {
            throw new ServiceException("username already taken").withError(403);
        }

        userDAO.createUser(new UserData(request.username(), request.password(), request.email()));
        String auth = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(auth, request.username()));

        return new RegisterResult(request.username(), auth);
    }

    public LoginResult login(LoginRequest request) throws ServiceException {
        UserData user = userDAO.getUser(request.username());
        if (user == null) throw ServiceException.UNAUTHORIZED;

        if (!user.password().equals(request.password())) throw ServiceException.UNAUTHORIZED;

        authDAO.logoutUsername(request.username());
        String auth = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(auth, request.username()));

        return new LoginResult(request.username(), auth);
    }

    public void logout(AuthData authToken) throws ServiceException {
        authDAO.deleteAuth(authToken.authToken());
    }
}

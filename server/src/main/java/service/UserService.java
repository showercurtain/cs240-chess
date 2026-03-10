package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    public record RegisterRequest(String username, String password, String email) {}
    public record LoginRequest(String username, String password) {}

    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public AuthData register(RegisterRequest request) throws ServiceException {
        if (userDAO.getUser(request.username()) != null) {
            throw new ServiceException("username already taken").withError(403);
        }

        userDAO.createUser(new UserData(request.username(), request.password(), request.email()));
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, request.username());
        authDAO.createAuth(auth);

        return auth;
    }

    public AuthData login(LoginRequest request) throws ServiceException {
        UserData user = userDAO.getUserAuth(request.username(), request.password());
        if (user == null) {
            throw ServiceException.UNAUTHORIZED;
        }

        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, request.username());
        authDAO.createAuth(auth);

        return auth;
    }

    public void logout(AuthData authToken) throws ServiceException {
        authDAO.deleteAuth(authToken.authToken());
    }
}

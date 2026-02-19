package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DataNotFound;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    public record RegisterRequest(String username, String password, String email) {}
    public record RegisterResult(String username, String authToken) {}
    public record LoginRequest(String username, String password) {}
    public record LoginResult(String username, String authToken) {}

    private AuthDAO authDAO;
    private UserDAO userDAO;

    public RegisterResult register(RegisterRequest request) throws ServiceException {
        try {
            userDAO.getUser(request.username());
        } catch (DataNotFound e) {
            throw new ServiceException("Error: username already taken").withError(403);
        } catch (DataAccessException e) {
            throw e.toServiceException();
        }

        try {
            userDAO.createUser(new UserData(request.username(), request.password(), request.email()));
        } catch (DataAccessException e) {
            throw e.toServiceException();
        }

        UUID auth = UUID.randomUUID();

        try {
            authDAO.createAuth(new AuthData(auth.toString(), request.username()));
        } catch (DataAccessException e) {
            throw e.toServiceException();
        }

        return new RegisterResult(request.username(), auth.toString());
    }
}

package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    HashMap<String, UserData> data;

    public MemoryUserDAO() {
        data = new HashMap<>();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        data.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return data.get(username);
    }

    @Override
    public void deleteUser(String username) throws DataAccessException {
        data.remove(username);
    }
}

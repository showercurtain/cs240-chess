package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.DataNotFound;
import dataaccess.UserDAO;
import model.AuthData;
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
        UserData out = data.get(username);
        if (out == null) throw new DataNotFound("User", username);
        return out;
    }

    @Override
    public void deleteUser(String username) throws DataAccessException {
        if (!data.containsKey(username)) throw new DataNotFound("User", username);
        data.remove(username);
    }
}

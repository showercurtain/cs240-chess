package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    HashMap<String, UserData> data;

    public MemoryUserDAO() {
        data = new HashMap<>();
    }

    @Override
    public void createUser(UserData user) {
        data.put(user.username(), user);
    }

    @Override
    public @Nullable UserData getUser(String username) {
        return data.get(username);
    }

    @Override
    public void deleteUser(String username) {
        data.remove(username);
    }
}

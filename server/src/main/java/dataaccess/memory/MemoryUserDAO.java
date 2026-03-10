package dataaccess.memory;

import dataaccess.UserDAO;
import model.UserData;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    final HashMap<String, UserData> data;

    public MemoryUserDAO() {
        data = new HashMap<>();
    }

    @Override
    public void createUser(UserData user) {
        synchronized (data) {
            data.put(user.username(), user);
        }
    }

    @Override
    public @Nullable UserData getUser(String username) {
        synchronized (data) {
            return data.get(username);
        }
    }

    @Override
    public void clear() {
        synchronized (data) {
            data.clear();
        }
    }

    @Override
    public void initTable() { }
}

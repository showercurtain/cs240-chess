package dataaccess.memory;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    final HashMap<String, AuthData> data;

    public MemoryAuthDAO() {
        data = new HashMap<>();
    }

    @Override
    public void createAuth(AuthData auth) {
        synchronized (data) {
            data.put(auth.authToken(), auth);
        }
    }

    @Override
    public @Nullable AuthData getAuth(String authToken) {
        synchronized (data) {
            return data.get(authToken);
        }
    }

    @Override
    public void deleteAuth(String authToken) {
        synchronized (data) {
            if (!data.containsKey(authToken)) {
                return;
            }
            data.remove(authToken);
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

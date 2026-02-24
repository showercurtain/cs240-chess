package dataaccess.memory;

import dataaccess.AuthDAO;
import model.AuthData;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Optional;

public class MemoryAuthDAO implements AuthDAO {
    HashMap<String, AuthData> data;

    public MemoryAuthDAO() {
        data = new HashMap<>();
    }

    @Override
    public void createAuth(AuthData auth) {
        data.put(auth.authToken(), auth);
    }

    @Override
    public @Nullable AuthData getAuth(String authToken) {
        return data.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        if (!data.containsKey(authToken)) return;
        data.remove(authToken);
    }

    @Override
    public void logoutUsername(String username) {
        Optional<AuthData> data = this.data.values().stream()
                .filter(auth -> auth.username().equals(username))
                .findFirst();

        data.ifPresent(auth -> this.data.remove(auth.authToken()));
    }
}

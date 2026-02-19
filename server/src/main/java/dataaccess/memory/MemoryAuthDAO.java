package dataaccess.memory;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    HashMap<String, AuthData> data;

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        data.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return data.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        data.remove(authToken);
    }
}

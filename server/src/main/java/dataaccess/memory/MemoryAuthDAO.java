package dataaccess.memory;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DataNotFound;
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
        AuthData out = data.get(authToken);
        if (out == null) throw new DataNotFound("Auth", authToken);
        return out;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!data.containsKey(authToken)) throw new DataNotFound("Auth", authToken);
        data.remove(authToken);
    }
}

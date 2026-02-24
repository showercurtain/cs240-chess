package dataaccess;

import model.AuthData;
import org.jetbrains.annotations.Nullable;

public interface AuthDAO {
    void createAuth(AuthData auth) throws DataAccessException;
    @Nullable
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;
}

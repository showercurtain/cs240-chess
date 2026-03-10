package dataaccess;

import model.UserData;
import org.jetbrains.annotations.Nullable;

public interface UserDAO {
    void createUser(UserData user) throws DataAccessException;
    @Nullable
    UserData getUser(String username) throws DataAccessException;
    void clear() throws DataAccessException;
    void initTable() throws DataAccessException;
    @Nullable
    UserData getUserAuth(String username, String password) throws DataAccessException;
}

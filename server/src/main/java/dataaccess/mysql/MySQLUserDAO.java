package dataaccess.mysql;

import dataaccess.UserDAO;
import model.UserData;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class MySQLUserDAO implements UserDAO {
    public MySQLUserDAO() {
    }

    @Override
    public void createUser(UserData user) {
    }

    @Override
    public @Nullable UserData getUser(String username) {
    }

    @Override
    public void clear() {
    }
}

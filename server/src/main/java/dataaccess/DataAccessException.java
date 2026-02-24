package dataaccess;

import service.ServiceException;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends ServiceException {
    public DataAccessException(String message) {
        super("Database error while processing request: " + message);
    }
    public DataAccessException(String message, Throwable ex) {
        super("Database error while processing request: " + message, ex);
    }
}

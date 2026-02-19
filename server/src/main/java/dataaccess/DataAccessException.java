package dataaccess;

import service.ServiceException;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception {
    public DataAccessException(String message) {
        super(message);
    }
    public DataAccessException(String message, Throwable ex) {
        super(message, ex);
    }

    public ServiceException toServiceException() {
        return new ServiceException("Database error while processing request: " + getMessage(), this);
    }
}

package dataaccess;

public class DataNotFound extends DataAccessException {
    public DataNotFound(String type, Object identifier) {
        super(String.format("No %s found with id \"%s\"", type, identifier));
    }

    public DataNotFound(String type, Object identifier, Throwable ex) {
        super(String.format("No %s found with id \"%s\"", type, identifier), ex);
    }
}

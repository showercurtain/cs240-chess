package dataaccess;

public class DataNotFound extends DataAccessException {
    public DataNotFound(String type, String identifier) {
        super(String.format("No %s found with id \"%s\"", type, identifier));
    }

    public DataNotFound(String type, String identifier, Throwable ex) {
        super(String.format("No %s found with id \"%s\"", type, identifier), ex);
    }
}

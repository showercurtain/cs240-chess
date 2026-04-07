package service;

public class ServiceException extends Exception {
    public static final ServiceException UNAUTHORIZED = new ServiceException("unauthorized",401);
    public static final ServiceException ALREADY_TAKEN = new ServiceException("already taken",403);
    public static final ServiceException NO_SUCH_GAME = new ServiceException("No such game",400);

    int httpError;

    public ServiceException(String message) {
        super(message);
        httpError = 500;
    }

    public ServiceException(String message, int httpError) {
        super(message);
        this.httpError = httpError;
    }

    public ServiceException(String message, Throwable ex) {
        super(message, ex);
        httpError = 500;
    }

    public ServiceException withError(int httpError) {
        return new ServiceException(getMessage(), httpError);
    }

    public ServiceException withMessage(String message) {
        return new ServiceException(message, httpError);
    }

    public int getHttpError() {
        return httpError;
    }
}

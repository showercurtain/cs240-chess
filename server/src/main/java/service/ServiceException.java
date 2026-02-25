package service;

public class ServiceException extends Exception {
    public static final ServiceException UNAUTHORIZED = new ServiceException("unauthorized").withError(401);

    int httpError;

    public ServiceException(String message) {
        super(message);
        httpError = 500;
    }

    public ServiceException(String message, Throwable ex) {
        super(message, ex);
        httpError = 500;
    }

    public ServiceException withError(int http_error) {
        this.httpError = http_error;
        return this;
    }

    public int getHttpError() {
        return httpError;
    }
}

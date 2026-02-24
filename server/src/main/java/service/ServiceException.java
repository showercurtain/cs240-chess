package service;

public class ServiceException extends Exception {
    public static final ServiceException UNAUTHORIZED = new ServiceException("unauthorized").withError(401);

    int http_error;

    public ServiceException(String message) {
        super(message);
        http_error = 500;
    }

    public ServiceException(String message, Throwable ex) {
        super(message, ex);
        http_error = 500;
    }

    public ServiceException withError(int http_error) {
        this.http_error = http_error;
        return this;
    }

    public int getHttpError() {
        return http_error;
    }
}

package service;

public class ServiceException extends Exception {
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
}

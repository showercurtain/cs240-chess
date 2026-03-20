package client;

public class ServerException extends Exception {
    int httpCode;

    public ServerException(String message, int httpCode) {
        super(message);
        this.httpCode = httpCode;
    }

    public int getHttpCode() {
        return httpCode;
    }
}

package dataaccess.exceptions;

public class UnauthorizedRequestException extends DataAccessException {
    public UnauthorizedRequestException(String message) {
        super(message);
    }
    public UnauthorizedRequestException(String message, Throwable ex) {
        super(message, ex);
    }
}

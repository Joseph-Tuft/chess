package dataaccess.Exceptions;

public class UnauthorizedRequestException extends DataAccessException {
    public UnauthorizedRequestException(String message) {
        super(message);
    }
    public UnauthorizedRequestException(String message, Throwable ex) {
        super(message, ex);
    }
}

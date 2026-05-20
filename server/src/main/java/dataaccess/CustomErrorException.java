package dataaccess;

public class CustomErrorException extends DataAccessException {
    public CustomErrorException(String message) {
        super(message);
    }
    public CustomErrorException(String message, Throwable ex) {
        super(message, ex);
    }
}

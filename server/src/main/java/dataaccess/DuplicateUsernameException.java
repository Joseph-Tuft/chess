package dataaccess;

public class DuplicateUsernameException extends DataAccessException {
    public DuplicateUsernameException(String message) {
        super(message);
    }
    public DuplicateUsernameException(String message, Throwable ex) {
        super(message, ex);
    }
}

package dataaccess;

public class IncompleteRequestException extends RuntimeException {
    public IncompleteRequestException(String message) {
        super(message);
    }
    public IncompleteRequestException(String message, Throwable ex) {
        super(message, ex);
    }
}

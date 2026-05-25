package dataaccess.exceptions;

import dataaccess.DataAccessSelector;

public class ResponseException extends DataAccessException {
    public ResponseException(String message) {
        super(message);
    }
    public ResponseException(String message, Throwable ex) {
        super(message, ex);
    }
}

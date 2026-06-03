package dataaccess;

import dataaccess.exceptions.DataAccessException;

public class ResponseException extends DataAccessException {
    public ResponseException(String message) {
        super(message);
    }
    public ResponseException(String message, Throwable ex) {
        super(message, ex);
    }
}

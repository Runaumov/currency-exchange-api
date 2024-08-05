package org.example.currencyexchangeapi.exceptions;

public class DatabaseConnectionException extends RuntimeException {

    public DatabaseConnectionException(String message) {
        super(message);
    }

}

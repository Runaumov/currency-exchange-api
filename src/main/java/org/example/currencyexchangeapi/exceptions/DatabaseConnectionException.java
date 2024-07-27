package org.example.currencyexchangeapi.exceptions;

import java.sql.SQLException;

public class DatabaseConnectionException extends RuntimeException {

    public DatabaseConnectionException(String message) {
        super(message);
    }

}

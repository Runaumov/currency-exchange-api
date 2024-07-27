package org.example.currencyexchangeapi.exceptions;

import java.sql.SQLException;

public class DatabaseConnectionException extends RuntimeException {

    public DatabaseConnectionException(String message, SQLException e) {
        super(message, e);
    }

    public DatabaseConnectionException(String message) {
        super(message);
    }

}

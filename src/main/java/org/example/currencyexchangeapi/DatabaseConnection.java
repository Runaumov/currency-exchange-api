package org.example.currencyexchangeapi;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.currencyexchangeapi.exceptions.DatabaseConnectionException;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DATABASE_URL =
            "jdbc:sqlite:D:\\Javadev\\currency-exchange-api\\src\\main\\resources\\currencyExchange.db";
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        config.setJdbcUrl(DATABASE_URL);
        ds = new HikariDataSource(config);
    }

    public static Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Error connecting to database", e);
        }
    }
}

package org.example.currencyexchangeapi;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ExampleDatabaseStatement {

    private static final String SQL = "Select * from currencies";

    public static void getStatement() throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(SQL);
            while (result.next()) {
                System.out.println(result.getString(1) + " " +
                        result.getString(2));
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        ExampleDatabaseStatement exampleDatabaseStatement = new ExampleDatabaseStatement();
        exampleDatabaseStatement.getStatement();
    }
}

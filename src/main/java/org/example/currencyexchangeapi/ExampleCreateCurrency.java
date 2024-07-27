package org.example.currencyexchangeapi;

import org.example.currencyexchangeapi.model.Currency;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ExampleCreateCurrency {

    private static final String SQL = "Select * from currencies WHERE id = 1";

    public void printStatement() throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(SQL);
            while (result.next()) {
                Currency currency = getCurrency(result);
                System.out.println(currency.toString());
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        ExampleCreateCurrency exampleCreateCurrency = new ExampleCreateCurrency();
        exampleCreateCurrency.printStatement();
    }

    public static Currency getCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getLong("id"),
                resultSet.getString("code"),
                resultSet.getString("fullname"),
                resultSet.getString("sign")
        );
    }
}

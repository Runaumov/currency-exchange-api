package org.example.currencyexchangeapi.dao;

import org.example.currencyexchangeapi.DatabaseConnection;
import org.example.currencyexchangeapi.exceptions.DatabaseConnectionException;
import org.example.currencyexchangeapi.model.Currency;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcCurrencyDao implements CurrencyDao {

    @Override
    public List<Currency> findAll() throws DatabaseConnectionException {
        String sql = "SELECT * FROM currencies";
        List<Currency> allCurrencies = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ){
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                allCurrencies.add(new Currency(resultSet.getLong("id"),
                        resultSet.getString("code"),
                        resultSet.getString("fullname"),
                        resultSet.getString("sign")));
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Database unavailable", e);
        }
        return allCurrencies;
    }

    @Override
    public Currency findCode(String code) {
        String sql = "SELECT * FROM currencies WHERE code=?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ){
            preparedStatement.setString(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return new Currency(
                        resultSet.getLong("id"),
                        resultSet.getString("code"),
                        resultSet.getString("fullname"),
                        resultSet.getString("sign")
                );
            }
        } catch (SQLException e) {
            // TO DO
        }

        return null;
    }

    @Override
    public void saveCurrency(Currency currency) {
        String sql = "INSERT INTO currencies (code, fullname, sign) VALUES (?,?,?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ){
            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getFullname());
            preparedStatement.setString(3, currency.getSign());

            preparedStatement.executeUpdate();
            //ResultSet resultSet = preparedStatement.executeQuery();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateCurrency(Currency currency) {
        String sql = "UPDATE currencies SET code=?, fullname=?, sign=? WHERE id=?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ){
            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getFullname());
            preparedStatement.setString(3, currency.getSign());
            preparedStatement.setLong(4, currency.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteCurrency(Currency currency) {
        String sql = "DELETE FROM currencies WHERE id=?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ){
            preparedStatement.setLong(1, currency.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
//        Currency zloty = new Currency("PLN", "Zloty", "zł");
//        JdbcCurrencyDao dao = new JdbcCurrencyDao();
//        dao.updateCurrency(zloty);
        JdbcCurrencyDao dao = new JdbcCurrencyDao();
        String code = "USD";
        Currency currency = dao.findCode(code);
        int a = 1;
    }

}

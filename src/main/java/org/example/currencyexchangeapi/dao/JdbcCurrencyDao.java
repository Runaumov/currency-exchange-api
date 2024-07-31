package org.example.currencyexchangeapi.dao;

import org.example.currencyexchangeapi.DatabaseConnection;
import org.example.currencyexchangeapi.exceptions.DatabaseConnectionException;
import org.example.currencyexchangeapi.exceptions.ModelAlreadyExistsException;
import org.example.currencyexchangeapi.exceptions.ModelNotFoundException;
import org.example.currencyexchangeapi.model.Currency;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            throw new DatabaseConnectionException("Database is not responding");
        }
        return allCurrencies;
    }

    @Override
    public Optional<Currency> findByCode(String code) {
        String sql = "SELECT * FROM currencies WHERE code=?";
        Currency currency = new Currency();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ){
            preparedStatement.setString(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                currency.setId(resultSet.getLong("id"));
                currency.setCode(resultSet.getString("code"));
                currency.setFullname(resultSet.getString("fullname"));
                currency.setSign(resultSet.getString("sign"));
                return Optional.of(currency);
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Database is not responding");
        }
        return Optional.empty();
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
        } catch (SQLException e) {
            if (e instanceof SQLiteException) {
                SQLiteException sqLiteException = (SQLiteException) e;
                int resultCode = sqLiteException.getResultCode().code;
                if (resultCode == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE.code) {
                    throw new ModelAlreadyExistsException(
                            String.format("Currency '%s' already exists.", currency.getCode())
                    );
                }
            } else {
                throw new DatabaseConnectionException("Database is not responding");
            }
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
            throw new DatabaseConnectionException("Database is not responding");
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
            throw new DatabaseConnectionException("Database is not responding");
        }
    }

}

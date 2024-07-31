package org.example.currencyexchangeapi.dao;

import org.example.currencyexchangeapi.DatabaseConnection;
import org.example.currencyexchangeapi.exceptions.DatabaseConnectionException;
import org.example.currencyexchangeapi.exceptions.ModelAlreadyExistsException;
import org.example.currencyexchangeapi.exceptions.ModelNotFoundException;
import org.example.currencyexchangeapi.model.Currency;
import org.example.currencyexchangeapi.model.ExchangeRate;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcExchangeRateDao {

    public List<ExchangeRate> findAll() {
        String sql = "SELECT er.id AS id, " +
                "bc.id AS bc_id, " +
                "bc.fullname AS bc_name, " +
                "bc.code AS bc_code, " +
                "bc.sign AS bc_sign, " +
                "tc.id AS tc_id, " +
                "tc.fullname AS tc_name, " +
                "tc.code AS tc_code, " +
                "tc.sign AS tc_sign, " +
                "er.rate AS rate " +
                "FROM exchange_rates er " +
                "JOIN currencies bc ON er.base_currency_id = bc.id " +
                "JOIN currencies tc ON er.target_currency_id = tc.id";
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                exchangeRates.add(
                        new ExchangeRate(resultSet.getLong("id"),
                            new Currency(resultSet.getLong("bc_id"),
                                resultSet.getString("bc_name"),
                                resultSet.getString("bc_code"),
                                resultSet.getString("bc_sign")
                        ),
                            new Currency(
                                resultSet.getLong("tc_id"),
                                resultSet.getString("tc_name"),
                                resultSet.getString("tc_code"),
                                resultSet.getString("tc_sign")),
                        resultSet.getBigDecimal("rate")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Database is not responding");
        }
        return exchangeRates;
    }

    public Optional<ExchangeRate> findByCodes(String baseCode, String targetCode) {
        String sql = "SELECT er.id AS id, " +
                "bc.id AS bc_id, " +
                "bc.fullname AS bc_name, " +
                "bc.code AS bc_code, " +
                "bc.sign AS bc_sign, " +
                "tc.id AS tc_id, " +
                "tc.fullname AS tc_name, " +
                "tc.code AS tc_code, " +
                "tc.sign AS tc_sign, " +
                "er.rate AS rate " +
                "FROM exchange_rates er " +
                "JOIN currencies bc ON er.base_currency_id = bc.id " +
                "JOIN currencies tc ON er.target_currency_id = tc.id " +
                "WHERE bc.code = ? AND tc.code = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, baseCode);
            preparedStatement.setString(2, targetCode);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                ExchangeRate exchangeRate = new ExchangeRate();
                exchangeRate.setId(resultSet.getLong("id"));
                exchangeRate.setBaseCurrency(
                        new Currency(resultSet.getLong("bc_id"),
                        resultSet.getString("bc_name"),
                        resultSet.getString("bc_code"),
                        resultSet.getString("bc_sign")
                        ));
                exchangeRate.setTargetCurrency(
                        new Currency(
                        resultSet.getLong("tc_id"),
                        resultSet.getString("tc_name"),
                        resultSet.getString("tc_code"),
                        resultSet.getString("tc_sign")
                        ));
                exchangeRate.setRate(resultSet.getBigDecimal("rate"));
                return Optional.of(exchangeRate);
            }

        } catch (SQLException e) {
            throw new DatabaseConnectionException("Database is not responding");
        }
        return Optional.empty();
    }

    public List<ExchangeRate> findByTargetCode(String targetCode) {
        String sql = "SELECT er.id AS id, " +
                "bc.id AS bc_id, " +
                "bc.fullname AS bc_name, " +
                "bc.code AS bc_code, " +
                "bc.sign AS bc_sign, " +
                "tc.id AS tc_id, " +
                "tc.fullname AS tc_name, " +
                "tc.code AS tc_code, " +
                "tc.sign AS tc_sign, " +
                "er.rate AS rate " +
                "FROM exchange_rates er " +
                "JOIN currencies bc ON er.base_currency_id = bc.id " +
                "JOIN currencies tc ON er.target_currency_id = tc.id " +
                "WHERE tc.code = ?";
        List<ExchangeRate> exchangeRates = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, targetCode);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                exchangeRates.add(
                        new ExchangeRate(resultSet.getLong("id"),
                                new Currency(resultSet.getLong("bc_id"),
                                        resultSet.getString("bc_name"),
                                        resultSet.getString("bc_code"),
                                        resultSet.getString("bc_sign")
                                ),
                                new Currency(
                                        resultSet.getLong("tc_id"),
                                        resultSet.getString("tc_name"),
                                        resultSet.getString("tc_code"),
                                        resultSet.getString("tc_sign")),
                                resultSet.getBigDecimal("rate")
                        ));
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Database is not responding");
        }
        return exchangeRates;
    }

    public void saveExchangeRate(ExchangeRate exchangeRate) {
        String sql = "INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setLong(1, exchangeRate.getBaseCurrency().getId());
            preparedStatement.setLong(2, exchangeRate.getTargetCurrency().getId());
            preparedStatement.setBigDecimal(3, exchangeRate.getRate());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            if (e instanceof SQLiteException) {
                SQLiteException sqLiteException = (SQLiteException) e;
                int resultCode = sqLiteException.getResultCode().code;
                if (resultCode == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE.code) {
                    throw new ModelAlreadyExistsException(
                            String.format("Exchange rate '%s' - '%s' already exists.",
                                    exchangeRate.getBaseCurrency().getCode(),
                                    exchangeRate.getTargetCurrency().getCode())
                    );
                }
            } else {
                throw new DatabaseConnectionException("Database is not responding");
            }
        }
    }

    public void updateExchangeRate(ExchangeRate exchangeRate) {
        String sql = "UPDATE exchange_rates SET rate=? WHERE id=?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setBigDecimal(1, exchangeRate.getRate());
            preparedStatement.setLong(2, exchangeRate.getId());
            int updatedRow = preparedStatement.executeUpdate();

            if (updatedRow == 0) {
                throw new ModelNotFoundException(String.format("Exchange rate '%s'-'%s' not found in database and cannot be updated.",
                        exchangeRate.getBaseCurrency().getCode(),
                        exchangeRate.getTargetCurrency().getCode()));
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Database is not responding");
        }
    }

//    public static void main(String[] args) {
//        JdbcExchangeRateDao jdbcExchangeRateDao = new JdbcExchangeRateDao();
//        List<ExchangeRate> cny = jdbcExchangeRateDao.findByTargetCode("CNY");
//        int a = 1;
//    }

}
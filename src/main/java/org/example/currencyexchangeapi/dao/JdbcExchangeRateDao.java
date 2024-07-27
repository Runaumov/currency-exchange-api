package org.example.currencyexchangeapi.dao;

import org.example.currencyexchangeapi.DatabaseConnection;
import org.example.currencyexchangeapi.model.Currency;
import org.example.currencyexchangeapi.model.ExchangeRate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcExchangeRateDao implements ExchangeDao {

    @Override
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
            // TO DO
        }
        return exchangeRates;
    }

    @Override
    public Optional<ExchangeRate> findByCode(String baseCode, String targetCode) {
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
            while (resultSet.next()) {
                return Optional.of(
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
            // TO DO
        }
        return Optional.empty(); // проработать
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
            // TO DO
        }

    }

    public void updateExchangeRate(ExchangeRate exchangeRate) {
        String sql = "UPDATE exchange_rates SET rate=? WHERE id=?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setBigDecimal(1, exchangeRate.getRate());
            preparedStatement.setLong(2, exchangeRate.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            // TO DO
        }
    }

}
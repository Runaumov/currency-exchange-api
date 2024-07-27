package org.example.currencyexchangeapi.service;

import org.example.currencyexchangeapi.dao.JdbcExchangeRateDao;
import org.example.currencyexchangeapi.dto.RequestExchangeDto;
import org.example.currencyexchangeapi.dto.ResponseExchangeDto;
import org.example.currencyexchangeapi.model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class Exchange {
    JdbcExchangeRateDao jdbcExchangeRateDao = new JdbcExchangeRateDao();

    public ResponseExchangeDto exchange(RequestExchangeDto requestExchangeDto) {
        ExchangeRate exchangeRate = findExchange(requestExchangeDto).orElseThrow(() -> new NullPointerException(
                String.format("Exchange rate s - s not found in the database")));

        BigDecimal amount = requestExchangeDto.getAmount();
        BigDecimal convertedAmount = exchangeRate.getRate().multiply(amount).setScale(2, RoundingMode.HALF_UP);

        ResponseExchangeDto responseExchangeDto = new ResponseExchangeDto(
                exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(),
                exchangeRate.getRate(),
                amount,
                convertedAmount
        );
        return responseExchangeDto;
    }

    private Optional<ExchangeRate> findExchange(RequestExchangeDto requestExchangeDto) {
        Optional<ExchangeRate> exchangeRate = findByDirectRate(requestExchangeDto);

        if (exchangeRate.isEmpty()) {
            exchangeRate = findByInverseRate(requestExchangeDto);
        }

        if (exchangeRate.isEmpty()) {
            exchangeRate = findByCrossRate(requestExchangeDto);
        }

        return exchangeRate;
    }


    private Optional<ExchangeRate> findByDirectRate(RequestExchangeDto requestExchangeDto) {
        return jdbcExchangeRateDao.findByCode(requestExchangeDto.getBaseCurrency(), requestExchangeDto.getTargetCurrency());
    }

    private Optional<ExchangeRate> findByInverseRate(RequestExchangeDto requestExchangeDto) {
        Optional<ExchangeRate> exchangeRate = jdbcExchangeRateDao.findByCode(requestExchangeDto.getTargetCurrency(), requestExchangeDto.getBaseCurrency());
        if (exchangeRate.isEmpty()) {
            return Optional.empty();
        } else {
            ExchangeRate exchangeInverseRate = exchangeRate.get();
            BigDecimal newAmount = BigDecimal.ONE.divide(exchangeInverseRate.getRate(), 2, RoundingMode.HALF_UP);
            exchangeInverseRate.setRate(newAmount);
            return Optional.of(exchangeInverseRate);
        }
    }

    private Optional<ExchangeRate> findByCrossRate(RequestExchangeDto requestExchangeDto) {
        Optional<ExchangeRate> exchangeRateBaseOptional = jdbcExchangeRateDao.findByCode("USD", requestExchangeDto.getBaseCurrency());
        Optional<ExchangeRate> exchangeRateTargetOptional = jdbcExchangeRateDao.findByCode("USD", requestExchangeDto.getTargetCurrency());

        if (exchangeRateBaseOptional.isEmpty() || exchangeRateTargetOptional.isEmpty()) {
            return Optional.empty();
        } else {
            ExchangeRate exchangeRateBase = exchangeRateBaseOptional.get();
            ExchangeRate exchangeRateTarget = exchangeRateTargetOptional.get();

            BigDecimal currencyToBaseCurrencyRate = exchangeRateBaseOptional.get().getRate();
            BigDecimal currencyToTargetCurrencyRate = exchangeRateTargetOptional.get().getRate();

            BigDecimal baseCurrencyToTargetCurrency = currencyToTargetCurrencyRate.divide(currencyToBaseCurrencyRate, 10, RoundingMode.HALF_UP);

            return Optional.of(new ExchangeRate(exchangeRateBase.getTargetCurrency(), exchangeRateTarget.getTargetCurrency(), baseCurrencyToTargetCurrency));
        }
    }


}

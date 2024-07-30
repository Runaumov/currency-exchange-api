package org.example.currencyexchangeapi.service;

import org.example.currencyexchangeapi.dao.JdbcExchangeRateDao;
import org.example.currencyexchangeapi.dto.RequestExchangeDto;
import org.example.currencyexchangeapi.dto.ResponseExchangeDto;
import org.example.currencyexchangeapi.exceptions.ModelNotFoundException;
import org.example.currencyexchangeapi.model.ExchangeRate;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ExchangeService {
    JdbcExchangeRateDao jdbcExchangeRateDao = new JdbcExchangeRateDao();

    public ResponseExchangeDto exchangeRateForAmount(RequestExchangeDto requestExchangeDto) {
        ExchangeRate exchangeRate = findExchangeRateForAmount(requestExchangeDto);

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

    private ExchangeRate findExchangeRateForAmount(RequestExchangeDto requestExchangeDto) {
        ExchangeRate exchangeRate = null;

        try {
            exchangeRate = findByDirectRate(requestExchangeDto);
        } catch (Exception ignored) {
        }

        if (exchangeRate == null) {
            try {
                exchangeRate = findByInverseRate(requestExchangeDto);
            } catch (Exception ignored) {
            }
        }

        if (exchangeRate == null) {
            try {
                exchangeRate = findByCrossRate(requestExchangeDto);
            } catch (Exception ignored) {
            }
        }

        if (exchangeRate == null) {
            throw new ModelNotFoundException(String.format(
                    "Exchange rate '%s'-'%s' does not found in database or cannot be found by cross rate",
                    requestExchangeDto.getBaseCurrency(),
                    requestExchangeDto.getTargetCurrency()
                    ));
        }

        return exchangeRate;
    }


    private ExchangeRate findByDirectRate(RequestExchangeDto requestExchangeDto) {
        return jdbcExchangeRateDao.findByCode(requestExchangeDto.getBaseCurrency(), requestExchangeDto.getTargetCurrency());
    }

    private ExchangeRate findByInverseRate(RequestExchangeDto requestExchangeDto) {
        ExchangeRate exchangeRate = jdbcExchangeRateDao.findByCode(requestExchangeDto.getTargetCurrency(), requestExchangeDto.getBaseCurrency());
        BigDecimal newAmount = BigDecimal.ONE.divide(exchangeRate.getRate(), 2, RoundingMode.HALF_UP);
        exchangeRate.setRate(newAmount);
        return exchangeRate;

    }

    private ExchangeRate findByCrossRate(RequestExchangeDto requestExchangeDto) {
        ExchangeRate exchangeRateBase = jdbcExchangeRateDao.findByCode("USD", requestExchangeDto.getBaseCurrency());
        ExchangeRate exchangeRateTarget = jdbcExchangeRateDao.findByCode("USD", requestExchangeDto.getTargetCurrency());

        BigDecimal currencyToBaseCurrencyRate = exchangeRateBase.getRate();
        BigDecimal currencyToTargetCurrencyRate = exchangeRateTarget.getRate();

        BigDecimal baseCurrencyToTargetCurrency = currencyToTargetCurrencyRate.divide(currencyToBaseCurrencyRate, 10, RoundingMode.HALF_UP);

        return new ExchangeRate(exchangeRateBase.getTargetCurrency(), exchangeRateTarget.getTargetCurrency(), baseCurrencyToTargetCurrency);
    }

}

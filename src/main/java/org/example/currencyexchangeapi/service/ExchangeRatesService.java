package org.example.currencyexchangeapi.service;

import org.example.currencyexchangeapi.dao.JdbcCurrencyDao;
import org.example.currencyexchangeapi.dao.JdbcExchangeRateDao;
import org.example.currencyexchangeapi.dto.RequestExchangeRateDto;
import org.example.currencyexchangeapi.dto.ResponseExchangeRateDto;
import org.example.currencyexchangeapi.exceptions.ModelNotFoundException;
import org.example.currencyexchangeapi.model.Currency;
import org.example.currencyexchangeapi.model.ExchangeRate;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;

public class ExchangeRatesService {
    JdbcExchangeRateDao jdbcExchangeRateDao = new JdbcExchangeRateDao();
    JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();
    ModelMapper modelMapper = new ModelMapper();

    public ResponseExchangeRateDto postExchangeRates(RequestExchangeRateDto requestExchangeRateDto) {
        String baseCode = requestExchangeRateDto.getBaseCurrencyCode();
        String targetCode = requestExchangeRateDto.getTargetCurrencyCode();

        Currency baseCurrency = jdbcCurrencyDao.findByCode(requestExchangeRateDto.getBaseCurrencyCode())
                .orElseThrow(() -> new ModelNotFoundException(String.format(
                        "Base currency '%s' not found in database.", baseCode)));
        Currency targetCurrency = jdbcCurrencyDao.findByCode(requestExchangeRateDto.getTargetCurrencyCode())
                .orElseThrow(() -> new ModelNotFoundException(String.format(
                        "Target currency '%s' not found in database.", targetCode)));

        ExchangeRate requestExchangeRate = new ExchangeRate(
                baseCurrency,
                targetCurrency,
                new BigDecimal(requestExchangeRateDto.getRate()));

        jdbcExchangeRateDao.saveExchangeRate(requestExchangeRate);

        ExchangeRate responseExchangeRate = jdbcExchangeRateDao.findByCodes(baseCode, targetCode).orElseThrow(() ->
                new ModelNotFoundException(String.format("Exchange rate '%s'-'%s' not found in database and cannot be added.",
                        baseCode, targetCode)));

        return modelMapper.map(responseExchangeRate, ResponseExchangeRateDto.class);
    }
}

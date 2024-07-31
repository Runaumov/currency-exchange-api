package org.example.currencyexchangeapi.service;

import org.example.currencyexchangeapi.dao.JdbcExchangeRateDao;
import org.example.currencyexchangeapi.dto.RequestExchangeRateDto;
import org.example.currencyexchangeapi.dto.ResponseExchangeRateDto;
import org.example.currencyexchangeapi.exceptions.ModelNotFoundException;
import org.example.currencyexchangeapi.model.ExchangeRate;
import org.modelmapper.ModelMapper;

public class ExchangeRateService {
    JdbcExchangeRateDao jdbcExchangeRateDao = new JdbcExchangeRateDao();
    ModelMapper modelMapper = new ModelMapper();

    public ResponseExchangeRateDto patchExchangeRate(RequestExchangeRateDto requestExchangeRateDto) {
        String baseCode = requestExchangeRateDto.getBaseCurrencyCode();
        String targetCode = requestExchangeRateDto.getTargetCurrencyCode();

        ExchangeRate requestExchangeRate = jdbcExchangeRateDao.findByCodes(
                requestExchangeRateDto.getBaseCurrencyCode(),
                requestExchangeRateDto.getTargetCurrencyCode()).orElseThrow(() ->
                new ModelNotFoundException(String.format("Exchange rate '%s'-'%s' not found in database and cannot be updated.",
                        baseCode, targetCode)));

        requestExchangeRate.setRate(requestExchangeRateDto.getRate());
        jdbcExchangeRateDao.updateExchangeRate(requestExchangeRate);

        ExchangeRate responseExchangeRate = jdbcExchangeRateDao.findByCodes(baseCode, targetCode).orElseThrow(() ->
                new ModelNotFoundException(String.format("Exchange rate '%s'-'%s' not found in database.",
                        baseCode, targetCode)));

        return modelMapper.map(responseExchangeRate, ResponseExchangeRateDto.class);
    }
}

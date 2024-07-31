package org.example.currencyexchangeapi.service;

import org.example.currencyexchangeapi.dao.JdbcExchangeRateDao;
import org.example.currencyexchangeapi.dto.RequestExchangeDto;
import org.example.currencyexchangeapi.dto.ResponseExchangeDto;
import org.example.currencyexchangeapi.exceptions.ModelNotFoundException;
import org.example.currencyexchangeapi.model.ExchangeRate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class ExchangeService {
    JdbcExchangeRateDao jdbcExchangeRateDao = new JdbcExchangeRateDao();

    public ResponseExchangeDto exchangeRateForAmount(RequestExchangeDto requestExchangeDto) {
        ExchangeRate exchangeRate = findExchangeRateForAmount(requestExchangeDto).orElseThrow(() ->
        new ModelNotFoundException(String.format(
                "Exchange rate '%s'-'%s' does not found in database or cannot be found by cross rate",
                requestExchangeDto.getBaseCurrency(),
                requestExchangeDto.getTargetCurrency()
        )));

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

    private Optional<ExchangeRate> findExchangeRateForAmount(RequestExchangeDto requestExchangeDto) {
        Optional<ExchangeRate> exchangeRateOptional = findByDirectRate(requestExchangeDto);

        if (exchangeRateOptional.isEmpty()) {
            exchangeRateOptional = findByInverseRate(requestExchangeDto);
        }
        if (exchangeRateOptional.isEmpty()) {
            exchangeRateOptional = findByCrossRate(requestExchangeDto);
        }
        return exchangeRateOptional;

    }

    private Optional<ExchangeRate> findByDirectRate(RequestExchangeDto requestExchangeDto) {
        return jdbcExchangeRateDao.findByCode(requestExchangeDto.getBaseCurrency(), requestExchangeDto.getTargetCurrency());
    }

    private Optional<ExchangeRate> findByInverseRate(RequestExchangeDto requestExchangeDto) {
        Optional<ExchangeRate> exchangeRate = jdbcExchangeRateDao.findByCode(requestExchangeDto.getTargetCurrency(), requestExchangeDto.getBaseCurrency());
        if (exchangeRate.isPresent()) {
            BigDecimal newAmount = BigDecimal.ONE.divide(exchangeRate.get().getRate(), 2, RoundingMode.HALF_UP);
            exchangeRate.get().setRate(newAmount);
            return exchangeRate;
        }
        return Optional.empty();
    }

    private Optional<ExchangeRate> findByCrossRate(RequestExchangeDto requestExchangeDto) {
        Optional<ExchangeRate> exchangeRateBase = jdbcExchangeRateDao.findByCode("USD", requestExchangeDto.getBaseCurrency());
        Optional<ExchangeRate> exchangeRateTarget = jdbcExchangeRateDao.findByCode("USD", requestExchangeDto.getTargetCurrency());

        if (exchangeRateBase.isPresent() && exchangeRateTarget.isPresent()){
            BigDecimal currencyToBaseCurrencyRate = exchangeRateBase.get().getRate();
            BigDecimal currencyToTargetCurrencyRate = exchangeRateTarget.get().getRate();
            BigDecimal baseCurrencyToTargetCurrency = currencyToTargetCurrencyRate.divide(
                    currencyToBaseCurrencyRate, 10, RoundingMode.HALF_UP);

            ExchangeRate exchangeRate = new ExchangeRate(
                    exchangeRateBase.get().getTargetCurrency(),
                    exchangeRateTarget.get().getTargetCurrency(),
                    baseCurrencyToTargetCurrency);
            return Optional.of(exchangeRate);
        }
        return Optional.empty();
    }

}

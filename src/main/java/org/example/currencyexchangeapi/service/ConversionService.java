package org.example.currencyexchangeapi.service;

import org.example.currencyexchangeapi.dao.JdbcExchangeRateDao;
import org.example.currencyexchangeapi.dto.RequestExchangeDto;
import org.example.currencyexchangeapi.dto.ResponseExchangeDto;
import org.example.currencyexchangeapi.exceptions.ModelNotFoundException;
import org.example.currencyexchangeapi.model.ExchangeRate;
import org.example.currencyexchangeapi.model.Currency;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO: Добавить сервисы для ExchangeRate
public class ConversionService {
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
        return jdbcExchangeRateDao.findByCodes(requestExchangeDto.getBaseCurrency(), requestExchangeDto.getTargetCurrency());
    }

    private Optional<ExchangeRate> findByInverseRate(RequestExchangeDto requestExchangeDto) {
        Optional<ExchangeRate> exchangeRate = jdbcExchangeRateDao.findByCodes(requestExchangeDto.getTargetCurrency(), requestExchangeDto.getBaseCurrency());
        if (exchangeRate.isPresent()) {
            BigDecimal newAmount = BigDecimal.ONE.divide(exchangeRate.get().getRate(), 2, RoundingMode.HALF_UP);
            exchangeRate.get().setRate(newAmount);
            return exchangeRate;
        }
        return Optional.empty();
    }

    private Optional<ExchangeRate> findByCrossRate(RequestExchangeDto requestExchangeDto) {
        String baseCode = requestExchangeDto.getBaseCurrency();
        String targetCode = requestExchangeDto.getTargetCurrency();
        Optional<String> commonCode = getBaseCurrency(baseCode, targetCode);

        if (commonCode.isPresent()) {
            Optional<ExchangeRate> exchangeRateBase = jdbcExchangeRateDao.findByCodes(commonCode.get(), baseCode);
            Optional<ExchangeRate> exchangeRateTarget = jdbcExchangeRateDao.findByCodes(commonCode.get(), targetCode);

            if (exchangeRateBase.isPresent() && exchangeRateTarget.isPresent()) {
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
        }
        return Optional.empty();
    }

    private Optional<String> getBaseCurrency(String baseCode, String targetCode) {
        List<String> baseCodesForBaseCurrencyCode = jdbcExchangeRateDao.findByTargetCode(baseCode).stream()
                .map(ExchangeRate::getBaseCurrency)
                .map(Currency::getCode)
                .collect(Collectors.toList());

        List<String> baseCodesForTargetCurrencyCode = jdbcExchangeRateDao.findByTargetCode(targetCode).stream()
                .map(ExchangeRate::getBaseCurrency)
                .map(Currency::getCode)
                .collect(Collectors.toList());

        Optional<String> commonCode = baseCodesForBaseCurrencyCode.stream()
                .filter(baseCodesForTargetCurrencyCode::contains)
                .findFirst();

        return commonCode;
    }


}
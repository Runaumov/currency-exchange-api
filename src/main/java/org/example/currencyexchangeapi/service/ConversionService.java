package org.example.currencyexchangeapi.service;

import org.example.currencyexchangeapi.dao.JdbcExchangeRateDao;
import org.example.currencyexchangeapi.dto.RequestConversionDto;
import org.example.currencyexchangeapi.dto.ResponseConversionDto;
import org.example.currencyexchangeapi.exceptions.ModelNotFoundException;
import org.example.currencyexchangeapi.model.ExchangeRate;
import org.example.currencyexchangeapi.model.Currency;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConversionService {

    JdbcExchangeRateDao jdbcExchangeRateDao = new JdbcExchangeRateDao();

    public ResponseConversionDto exchangeRateForAmount(RequestConversionDto requestConversionDto) {
        ExchangeRate exchangeRate = findExchangeRateForAmount(requestConversionDto).orElseThrow(() ->
        new ModelNotFoundException(String.format(
                "Exchange rate '%s'-'%s' does not found in database or cannot be found by cross rate",
                requestConversionDto.getBaseCurrencyCode(),
                requestConversionDto.getTargetCurrencyCode()
        )));

        BigDecimal amount = new BigDecimal(requestConversionDto.getAmount());
        BigDecimal convertedAmount = exchangeRate.getRate().multiply(amount).setScale(2, RoundingMode.HALF_EVEN);

        ResponseConversionDto responseConversionDto = new ResponseConversionDto(
                exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(),
                exchangeRate.getRate(),
                amount,
                convertedAmount
        );

        return responseConversionDto;
    }

    private Optional<ExchangeRate> findExchangeRateForAmount(RequestConversionDto requestConversionDto) {
        Optional<ExchangeRate> exchangeRateOptional = findByDirectRate(requestConversionDto);

        if (exchangeRateOptional.isEmpty()) {
            exchangeRateOptional = findByInverseRate(requestConversionDto);
        }

        if (exchangeRateOptional.isEmpty()) {
            exchangeRateOptional = findByCrossRate(requestConversionDto);
        }

        return exchangeRateOptional;
    }

    private Optional<ExchangeRate> findByDirectRate(RequestConversionDto requestConversionDto) {
        return jdbcExchangeRateDao.findByCodes(requestConversionDto.getBaseCurrencyCode(),
                requestConversionDto.getTargetCurrencyCode());
    }

    private Optional<ExchangeRate> findByInverseRate(RequestConversionDto requestConversionDto) {
        Optional<ExchangeRate> exchangeRate = jdbcExchangeRateDao.findByCodes(
                requestConversionDto.getTargetCurrencyCode(),
                requestConversionDto.getBaseCurrencyCode());

        if (exchangeRate.isPresent()) {
            BigDecimal newAmount = BigDecimal.ONE.divide(exchangeRate.get().getRate(), 10, RoundingMode.HALF_EVEN);
            exchangeRate.get().setRate(newAmount);
            return exchangeRate;
        }

        return Optional.empty();
    }

    private Optional<ExchangeRate> findByCrossRate(RequestConversionDto requestConversionDto) {
        String baseCode = requestConversionDto.getBaseCurrencyCode();
        String targetCode = requestConversionDto.getTargetCurrencyCode();
        Optional<String> commonCode = getBaseCurrency(baseCode, targetCode);

        if (commonCode.isPresent()) {
            Optional<ExchangeRate> exchangeRateBase = jdbcExchangeRateDao.findByCodes(commonCode.get(), baseCode);
            Optional<ExchangeRate> exchangeRateTarget = jdbcExchangeRateDao.findByCodes(commonCode.get(), targetCode);

            if (exchangeRateBase.isPresent() && exchangeRateTarget.isPresent()) {
                BigDecimal currencyToBaseCurrencyRate = exchangeRateBase.get().getRate();
                BigDecimal currencyToTargetCurrencyRate = exchangeRateTarget.get().getRate();
                BigDecimal baseCurrencyToTargetCurrency = currencyToTargetCurrencyRate.divide(
                        currencyToBaseCurrencyRate, 10, RoundingMode.HALF_EVEN);

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
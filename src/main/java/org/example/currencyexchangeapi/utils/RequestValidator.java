package org.example.currencyexchangeapi.utils;

import org.example.currencyexchangeapi.dto.RequestConversionDto;
import org.example.currencyexchangeapi.dto.RequestCurrencyDto;
import org.example.currencyexchangeapi.dto.RequestExchangeRateDto;
import org.example.currencyexchangeapi.exceptions.InvalidRequestException;
import java.math.BigDecimal;
import java.util.regex.Pattern;

public class RequestValidator {

    private static final Pattern CURRENCY_CODE_PATTERN = Pattern.compile("^[A-Za-z]{3}$");
    private static final Pattern CURRENCY_FULLNAME_PATTERN = Pattern.compile("^[A-Za-z\\s\\(\\)]+$");
    private static final Pattern CURRENCY_SIGN_PATTERN = Pattern.compile("^[\\p{Sc}A-Za-z]+$");
    private static final Pattern RATE_AND_AMOUNT_PATTERN = Pattern.compile("\\d+(\\.\\d{2})?");

    public static void validateRequestCurrencyDto(RequestCurrencyDto requestCurrencyDto) {
        validateCurrencyCode(requestCurrencyDto.getCode());
        validateCurrencyFullname(requestCurrencyDto.getFullname());
        validateCurrencySign(requestCurrencyDto.getSign());
    }

    public static void validateRequestExchangeRateDto(RequestExchangeRateDto requestExchangeRateDto) {
        validateCurrencyCode(requestExchangeRateDto.getBaseCurrencyCode());
        validateCurrencyCode(requestExchangeRateDto.getTargetCurrencyCode());
        validateRateAndAmount(requestExchangeRateDto.getRate());
    }

    public static void validateRequestConversionDto(RequestConversionDto requestConversionDto) {
        validateCurrencyCode(requestConversionDto.getBaseCurrencyCode());
        validateCurrencyCode(requestConversionDto.getTargetCurrencyCode());
        validateRateAndAmount(requestConversionDto.getAmount());
    }

    private static void validateRateAndAmount(String rateAndAmount) {
        if (rateAndAmount == null || !RATE_AND_AMOUNT_PATTERN.matcher(rateAndAmount).matches()) {
            throw new InvalidRequestException("Invalid rate or amount");
        }
    }

    public static void validateCurrencyCode(String code) {
        if (code == null || !CURRENCY_CODE_PATTERN.matcher(code).matches()) {
            throw new InvalidRequestException("Invalid currency code");
        }
    }

    public static void validateCurrencyFullname(String fullname) {
        if (fullname == null || !CURRENCY_FULLNAME_PATTERN.matcher(fullname).matches()) {
            throw new InvalidRequestException("Invalid currency name");
        }
    }

    public static void validateCurrencySign(String sign) {
        if (sign == null || !CURRENCY_SIGN_PATTERN.matcher(sign).matches()) {
            throw new InvalidRequestException("Invalid sign name");
        }
    }

}

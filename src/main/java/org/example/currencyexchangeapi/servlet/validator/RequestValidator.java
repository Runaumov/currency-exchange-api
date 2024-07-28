package org.example.currencyexchangeapi.servlet.validator;

import org.example.currencyexchangeapi.dto.RequestCurrencyDto;
import org.example.currencyexchangeapi.exceptions.InvalidRequestException;

import java.util.regex.Pattern;

public class RequestValidator {

    private static final Pattern CURRENCY_CODE_PATTERN = Pattern.compile("^[A-Za-z]{3}$");
    private static final Pattern CURRENCY_FULLNAME_PATTERN = Pattern.compile("^[A-Za-z]+ [A-Za-z]+$");
    private static final Pattern CURRENCY_SIGN_PATTERN = Pattern.compile("^[\\p{Sc}A-Za-z]$");

    public static void validateCurrency(RequestCurrencyDto requestCurrencyDto) {
        validateCurrencyCode(requestCurrencyDto.getCode());
        validateCurrencyFullname(requestCurrencyDto.getFullname());
        validateCurrencySign(requestCurrencyDto.getSign());
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
package org.example.currencyexchangeapi.servlet.validator;

import org.example.currencyexchangeapi.exceptions.InvalidRequestException;

import java.util.regex.Pattern;

public class RequestValidator {

    private static final Pattern CURRENCY_CODE_PATTERN = Pattern.compile("^[A-Za-z]{3}$");

    public static void validateCurrencyCode(String code) {
        if (code == null || !CURRENCY_CODE_PATTERN.matcher(code).matches()) {
            throw new InvalidRequestException("Invalid currency code");
        }
    }
}

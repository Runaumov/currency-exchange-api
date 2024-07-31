package org.example.currencyexchangeapi.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.currencyexchangeapi.dao.JdbcCurrencyDao;
import org.example.currencyexchangeapi.dto.ResponseCurrencyDto;
import org.example.currencyexchangeapi.model.Currency;
import org.example.currencyexchangeapi.servlet.validator.RequestValidator;
import org.example.currencyexchangeapi.exceptions.ModelNotFoundException;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = req.getPathInfo().replaceAll("/", "");
        RequestValidator.validateCurrencyCode(code);

        Currency currency = jdbcCurrencyDao.findByCode(code).orElseThrow(() ->
                new ModelNotFoundException(String.format("Currency '%s' not found in database.", code)));

        ResponseCurrencyDto responseCurrencyDto = new ResponseCurrencyDto(
                currency.getId(),
                currency.getCode(),
                currency.getFullname(),
                currency.getSign()
                );

        objectMapper.writeValue(resp.getWriter(), responseCurrencyDto);
    }

}

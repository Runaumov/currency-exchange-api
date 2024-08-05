package org.example.currencyexchangeapi.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.currencyexchangeapi.dao.JdbcCurrencyDao;
import org.example.currencyexchangeapi.dto.ResponseCurrencyDto;
import org.example.currencyexchangeapi.model.Currency;
import org.example.currencyexchangeapi.utils.RequestValidator;
import org.example.currencyexchangeapi.exceptions.ModelNotFoundException;
import org.modelmapper.ModelMapper;
import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();
    private final ModelMapper modelMapper = new ModelMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = req.getPathInfo().replaceAll("/", "");

        RequestValidator.validateCurrencyCode(code);

        Currency currency = jdbcCurrencyDao.findByCode(code).orElseThrow(() ->
                new ModelNotFoundException(String.format("Currency '%s' not found in database.", code)));

        ResponseCurrencyDto responseCurrencyDto = modelMapper.map(currency, ResponseCurrencyDto.class);

        objectMapper.writeValue(resp.getWriter(), responseCurrencyDto);
    }

}

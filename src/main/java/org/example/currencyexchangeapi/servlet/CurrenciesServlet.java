package org.example.currencyexchangeapi.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.currencyexchangeapi.dao.JdbcCurrencyDao;
import org.example.currencyexchangeapi.dto.RequestCurrencyDto;
import org.example.currencyexchangeapi.dto.ResponseCurrencyDto;
import org.example.currencyexchangeapi.exceptions.ModelNotFoundException;
import org.example.currencyexchangeapi.model.Currency;
import org.example.currencyexchangeapi.utils.RequestValidator;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final ModelMapper modelMapper = new ModelMapper();
    private final JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Currency> currencies = jdbcCurrencyDao.findAll();

        List<ResponseCurrencyDto> responseCurrenciesDto = new ArrayList<>();
        for (Currency currency : currencies) {
            responseCurrenciesDto.add(modelMapper.map(currency, ResponseCurrencyDto.class));
        }

        objectMapper.writeValue(resp.getWriter(), responseCurrenciesDto);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String code = req.getParameter("code");
        String fullname = req.getParameter("name");
        String sign = req.getParameter("sign");

        RequestCurrencyDto requestCurrencyDto = new RequestCurrencyDto(code, fullname, sign);
        RequestValidator.validateRequestCurrencyDto(requestCurrencyDto);

        jdbcCurrencyDao.saveCurrency(modelMapper.map(requestCurrencyDto, Currency.class));

        Currency responseCurrency = jdbcCurrencyDao.findByCode(requestCurrencyDto.getCode()).orElseThrow(() ->
                new ModelNotFoundException(String.format("Currency '%s' not found in database.", code)));

        ResponseCurrencyDto responseCurrencyDto = modelMapper.map(responseCurrency, ResponseCurrencyDto.class);

        objectMapper.writeValue(resp.getWriter(), responseCurrencyDto);
    }
}

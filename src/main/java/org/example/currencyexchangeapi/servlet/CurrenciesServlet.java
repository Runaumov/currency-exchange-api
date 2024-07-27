package org.example.currencyexchangeapi.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.currencyexchangeapi.dao.JdbcCurrencyDao;
import org.example.currencyexchangeapi.dto.RequestCurrencyDto;
import org.example.currencyexchangeapi.dto.ResponseCurrencyDto;
import org.example.currencyexchangeapi.model.Currency;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Currency> currencies = jdbcCurrencyDao.findAll();

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // наверное, это стоит вынести в отделный метод convert или что-то похожее
        List<ResponseCurrencyDto> responseCurrencyDto = new ArrayList<>();
        for (Currency currency : currencies) {
            responseCurrencyDto.add(new ResponseCurrencyDto(currency.getId(), currency.getCode(), currency.getFullname(), currency.getSign()));
        }

        objectMapper.writeValue(resp.getWriter(), responseCurrencyDto);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String code = req.getParameter("code");
        String fullname = req.getParameter("fullname");
        String sign = req.getParameter("sign");

        RequestCurrencyDto requestCurrencyDto = new RequestCurrencyDto(code, fullname, sign);

        // наверное, это стоит вынести в отделный метод convert или что-то похожее
        Currency currency = new Currency(requestCurrencyDto.getCode(), requestCurrencyDto.getFullname(), requestCurrencyDto.getSign());

        jdbcCurrencyDao.saveCurrency(currency);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // тут хня, возвращаем то, что получили, наверное, нужно возвращать с бд
        ResponseCurrencyDto responseCurrencyDto = new ResponseCurrencyDto(
                currency.getId(),
                currency.getCode(),
                currency.getFullname(),
                currency.getSign()
        );

        objectMapper.writeValue(resp.getWriter(), responseCurrencyDto);
    }
}

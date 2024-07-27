package org.example.currencyexchangeapi.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.currencyexchangeapi.dao.JdbcCurrencyDao;
import org.example.currencyexchangeapi.dto.ResponseCurrencyDto;
import org.example.currencyexchangeapi.model.Currency;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getPathInfo().replaceAll("/", "");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Currency currency = jdbcCurrencyDao.findCode(code); // что будет, если кода этого нет? м?

        ResponseCurrencyDto responseCurrencyDto = new ResponseCurrencyDto(
                currency.getId(),
                currency.getCode(),
                currency.getFullname(),
                currency.getSign()
                );

        objectMapper.writeValue(resp.getWriter(), responseCurrencyDto);
    }

}

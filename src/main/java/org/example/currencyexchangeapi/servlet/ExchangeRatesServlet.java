package org.example.currencyexchangeapi.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.currencyexchangeapi.dao.JdbcCurrencyDao;
import org.example.currencyexchangeapi.dao.JdbcExchangeRateDao;
import org.example.currencyexchangeapi.dto.RequestExchangeRateDto;
import org.example.currencyexchangeapi.dto.ResponseExchangeRateDto;
import org.example.currencyexchangeapi.model.ExchangeRate;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final JdbcExchangeRateDao jdbcExchangeRateDao = new JdbcExchangeRateDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ExchangeRate> exchangeRates = jdbcExchangeRateDao.findAll();

        List<ResponseExchangeRateDto> responseExchangeRateDto = new ArrayList<>();
        for (ExchangeRate exchangeRate : exchangeRates) {
            responseExchangeRateDto.add(
                    new ResponseExchangeRateDto(
                            exchangeRate.getId(),
                            exchangeRate.getBaseCurrency(),
                            exchangeRate.getTargetCurrency(),
                            exchangeRate.getRate()
                    )
            );
        }

        objectMapper.writeValue(resp.getWriter(), responseExchangeRateDto);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();

        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

        RequestExchangeRateDto requestExchangeRateDto = new RequestExchangeRateDto(baseCurrencyCode, targetCurrencyCode, rate);

        ExchangeRate requestExchangeRate = new ExchangeRate(
                jdbcCurrencyDao.findByCode(requestExchangeRateDto.getBaseCurrency()),
                jdbcCurrencyDao.findByCode(requestExchangeRateDto.getTargetCurrency()),
                new BigDecimal(requestExchangeRateDto.getRate()));

        jdbcExchangeRateDao.saveExchangeRate(requestExchangeRate);

        ExchangeRate responseExchangeRate = jdbcExchangeRateDao.findByCode(baseCurrencyCode, targetCurrencyCode);

        ResponseExchangeRateDto responseExchangeRateDto = new ResponseExchangeRateDto(
                responseExchangeRate.getId(),
                responseExchangeRate.getBaseCurrency(),
                responseExchangeRate.getTargetCurrency(),
                responseExchangeRate.getRate()
        );

        objectMapper.writeValue(resp.getWriter(), responseExchangeRateDto);

    }
}

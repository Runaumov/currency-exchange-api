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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<ExchangeRate> exchangeRates = jdbcExchangeRateDao.findAll();

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // наверное, это стоит вынести в отделный метод convert или что-то похожее
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();

        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

        RequestExchangeRateDto requestExchangeRateDto = new RequestExchangeRateDto(baseCurrencyCode, targetCurrencyCode, rate);

        // выглядит сомнительно
        ExchangeRate exchangeRate = new ExchangeRate(
                jdbcCurrencyDao.findByCode(requestExchangeRateDto.getBaseCurrency()),
                jdbcCurrencyDao.findByCode(requestExchangeRateDto.getTargetCurrency()),
                new BigDecimal(requestExchangeRateDto.getRate()));

        jdbcExchangeRateDao.saveExchangeRate(exchangeRate);

        // тут хня, возвращаем то, что получили, наверное, нужно возвращать с бд
        ResponseExchangeRateDto responseExchangeRateDto = new ResponseExchangeRateDto(
                exchangeRate.getId(),
                exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(),
                exchangeRate.getRate()
        );

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(resp.getWriter(), responseExchangeRateDto);

    }
}

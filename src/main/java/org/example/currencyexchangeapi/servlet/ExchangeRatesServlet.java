package org.example.currencyexchangeapi.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.currencyexchangeapi.dao.JdbcExchangeRateDao;
import org.example.currencyexchangeapi.dto.RequestExchangeRateDto;
import org.example.currencyexchangeapi.dto.ResponseExchangeRateDto;
import org.example.currencyexchangeapi.model.ExchangeRate;
import org.example.currencyexchangeapi.service.ExchangeRatesService;
import org.example.currencyexchangeapi.utils.RequestValidator;
import org.modelmapper.ModelMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final JdbcExchangeRateDao jdbcExchangeRateDao = new JdbcExchangeRateDao();
    private final ModelMapper modelMapper = new ModelMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ExchangeRate> exchangeRates = jdbcExchangeRateDao.findAll();

        List<ResponseExchangeRateDto> responseExchangeRateDto = new ArrayList<>();
        for (ExchangeRate exchangeRate : exchangeRates) {
            responseExchangeRateDto.add(modelMapper.map(exchangeRate, ResponseExchangeRateDto.class));
        }

        objectMapper.writeValue(resp.getWriter(), responseExchangeRateDto);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCode = req.getParameter("baseCurrencyCode");
        String targetCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

        RequestExchangeRateDto requestExchangeRateDto = new RequestExchangeRateDto(baseCode, targetCode, rate);
        RequestValidator.validateRequestExchangeRateDto(requestExchangeRateDto);

        ExchangeRatesService exchangeRatesService = new ExchangeRatesService();
        ResponseExchangeRateDto responseExchangeRateDto = exchangeRatesService.postExchangeRates(requestExchangeRateDto);

        objectMapper.writeValue(resp.getWriter(), responseExchangeRateDto);

    }
}

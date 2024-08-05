package org.example.currencyexchangeapi.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.currencyexchangeapi.dao.JdbcExchangeRateDao;
import org.example.currencyexchangeapi.dto.RequestExchangeRateDto;
import org.example.currencyexchangeapi.dto.ResponseExchangeRateDto;
import org.example.currencyexchangeapi.exceptions.ModelNotFoundException;
import org.example.currencyexchangeapi.model.ExchangeRate;
import org.example.currencyexchangeapi.service.ExchangeRateService;
import org.example.currencyexchangeapi.utils.RequestValidator;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private final JdbcExchangeRateDao jdbcExchangeRateDao = new JdbcExchangeRateDao();
    private final ModelMapper modelMapper = new ModelMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCode = req.getPathInfo().replaceAll("/", "").substring(0, 3);
        String targetCode = req.getPathInfo().replaceAll("/", "").substring(3);

        RequestValidator.validateCurrencyCode(baseCode);
        RequestValidator.validateCurrencyCode(targetCode);

        ExchangeRate exchangeRate = jdbcExchangeRateDao.findByCodes(baseCode, targetCode).orElseThrow(() ->
                new ModelNotFoundException(String.format("Exchange rate '%s'-'%s' not found in database",
                        baseCode, targetCode)));

        ResponseExchangeRateDto responseExchangeRateDto = modelMapper.map(exchangeRate, ResponseExchangeRateDto.class);
        objectMapper.writeValue(resp.getWriter(), responseExchangeRateDto);
    }

    private void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCode = req.getPathInfo().replaceAll("/", "").substring(0, 3);
        String targetCode = req.getPathInfo().replaceAll("/", "").substring(3);
        String parameter = req.getReader().readLine();
        String rate = parameter.replace("rate=", "");

        RequestExchangeRateDto requestExchangeRateDto = new RequestExchangeRateDto(baseCode, targetCode, rate);
        RequestValidator.validateRequestExchangeRateDto(requestExchangeRateDto);

        ExchangeRateService exchangeRateService = new ExchangeRateService();
        ResponseExchangeRateDto responseExchangeRateDto = exchangeRateService.patchExchangeRate(requestExchangeRateDto);
        objectMapper.writeValue(resp.getWriter(), responseExchangeRateDto);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if (method.equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }
}
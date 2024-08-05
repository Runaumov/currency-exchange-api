package org.example.currencyexchangeapi.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.currencyexchangeapi.dto.RequestConversionDto;
import org.example.currencyexchangeapi.dto.ResponseConversionDto;
import org.example.currencyexchangeapi.service.ConversionService;
import org.example.currencyexchangeapi.utils.RequestValidator;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchange")
public class ConversionServlet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrency = req.getParameter("from");
        String targetCurrency = req.getParameter("to");
        String amount = req.getParameter("amount");

        RequestConversionDto requestConversionDto = new RequestConversionDto(baseCurrency,
                targetCurrency, amount);

        RequestValidator.validateRequestConversionDto(requestConversionDto);

        ConversionService conversionService = new ConversionService();
        ResponseConversionDto responseConversionDto = conversionService.exchangeRateForAmount(requestConversionDto);

        objectMapper.writeValue(resp.getWriter(), responseConversionDto);
    }
}
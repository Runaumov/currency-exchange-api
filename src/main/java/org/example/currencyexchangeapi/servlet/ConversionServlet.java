package org.example.currencyexchangeapi.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.currencyexchangeapi.dto.RequestExchangeDto;
import org.example.currencyexchangeapi.dto.ResponseExchangeDto;
import org.example.currencyexchangeapi.service.ConversionService;
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

        RequestExchangeDto requestExchangeDto = new RequestExchangeDto(baseCurrency, targetCurrency, new BigDecimal(amount));

        ConversionService conversionService = new ConversionService();
        ResponseExchangeDto responseExchangeDto = conversionService.exchangeRateForAmount(requestExchangeDto);

        objectMapper.writeValue(resp.getWriter(), responseExchangeDto);
    }
}

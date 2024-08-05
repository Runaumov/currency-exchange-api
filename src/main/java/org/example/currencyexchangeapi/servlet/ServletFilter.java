package org.example.currencyexchangeapi.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.example.currencyexchangeapi.dto.ResponseErrorDto;
import org.example.currencyexchangeapi.exceptions.DatabaseConnectionException;
import org.example.currencyexchangeapi.exceptions.InvalidRequestException;
import org.example.currencyexchangeapi.exceptions.ModelAlreadyExistsException;
import org.example.currencyexchangeapi.exceptions.ModelNotFoundException;
import java.io.IOException;

@WebFilter("/*")
public class ServletFilter implements Filter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (DatabaseConnectionException e) {
            handleException(httpServletResponse, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (ModelAlreadyExistsException e) {
            handleException(httpServletResponse, e.getMessage(), HttpServletResponse.SC_CONFLICT);
        } catch (ModelNotFoundException e) {
            handleException(httpServletResponse, e.getMessage(), HttpServletResponse.SC_NOT_FOUND);
        } catch (InvalidRequestException e) {
            handleException(httpServletResponse, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void handleException(HttpServletResponse response, String message, int statusCode) throws IOException {
        ResponseErrorDto responseErrorDto = new ResponseErrorDto(message);
        String jsonResponse = objectMapper.writeValueAsString(responseErrorDto);
        response.setStatus(statusCode);
        response.getWriter().write(jsonResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}

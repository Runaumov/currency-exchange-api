package org.example.currencyexchangeapi.servlet;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.example.currencyexchangeapi.exceptions.DatabaseConnectionException;
import org.example.currencyexchangeapi.exceptions.InvalidRequestException;
import org.example.currencyexchangeapi.exceptions.ModelAlreadyExistsException;
import org.example.currencyexchangeapi.exceptions.ModelNotFoundException;

import java.io.IOException;

@WebFilter("/*")
public class ServletFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        try {
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("UTF-8");
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (DatabaseConnectionException e) {
            ((HttpServletResponse) servletResponse).setStatus(500);
        } catch (ModelAlreadyExistsException e) {
            ((HttpServletResponse) servletResponse).setStatus(409);
        } catch (ModelNotFoundException e) {
            ((HttpServletResponse) servletResponse).setStatus(404);
        } catch (InvalidRequestException e) {
            ((HttpServletResponse) servletResponse).setStatus(400);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}

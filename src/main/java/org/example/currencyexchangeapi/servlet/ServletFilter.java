package org.example.currencyexchangeapi.servlet;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.currencyexchangeapi.exceptions.DatabaseConnectionException;
import org.example.currencyexchangeapi.exceptions.ModelAlreadyExistsException;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

@WebFilter("/*")
public class ServletFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (DatabaseConnectionException e) {
            if (servletResponse instanceof HttpServletResponse) {
                ((HttpServletResponse) servletResponse).setStatus(500);
            }
        } catch (ModelAlreadyExistsException e) {
            if (servletResponse instanceof HttpServletResponse) {
                ((HttpServletResponse) servletResponse).setStatus(409);
            }
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}

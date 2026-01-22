package com.example.steammicro.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

//@Component
@Order(2)
public class PerformanceWarningFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(PerformanceWarningFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        long startTime = System.currentTimeMillis();

        try {
            if (request.getRequestURI().startsWith("/api")) {
                log.info("Request time measure started: {} {}", request.getMethod(), request.getRequestURI());
            }

            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            if (duration > 500L && request.getRequestURI().startsWith("/api")) {
                log.warn("Slow request detected: {} {} took {}ms",
                        request.getMethod(), request.getRequestURI(), duration);
            }

        }
    }
}

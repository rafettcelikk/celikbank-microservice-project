package com.rafetcelik.gatewayserver.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Order(2)
@Component
public class ResponseTraceFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(ResponseTraceFilter.class);

    @Autowired
    private FilterUtility filterUtility;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String correlationId = filterUtility.getCorrelationId(request);

        if (correlationId != null) {
            logger.debug("celikbank-correlation-id yanıta (response) ekleniyor: {}", correlationId);
            response.setHeader(FilterUtility.CORRELATION_ID, correlationId);
        }

        filterChain.doFilter(request, response);
    }
}
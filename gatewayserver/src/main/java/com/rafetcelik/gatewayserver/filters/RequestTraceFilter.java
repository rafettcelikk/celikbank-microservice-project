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
import java.util.UUID;

@Order(1)
@Component
public class RequestTraceFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestTraceFilter.class);

    @Autowired
    private FilterUtility filterUtility;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String correlationId = filterUtility.getCorrelationId(request);

        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
            logger.debug("{} bulunamadı. Yeni oluşturuldu: {}", FilterUtility.CORRELATION_ID, correlationId);
        } else {
            logger.debug("{} header'ı istekte bulundu: {}", FilterUtility.CORRELATION_ID, correlationId);
        }
        HttpServletRequest wrappedRequest = filterUtility.setCorrelationId(request, correlationId);

        response.setHeader(FilterUtility.CORRELATION_ID, correlationId);

        filterChain.doFilter(wrappedRequest, response);
    }
}
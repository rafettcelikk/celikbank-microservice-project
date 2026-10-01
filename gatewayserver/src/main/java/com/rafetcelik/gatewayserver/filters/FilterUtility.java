package com.rafetcelik.gatewayserver.filters;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class FilterUtility {

    public static final String CORRELATION_ID = "celikbank-correlation-id";

    public String getCorrelationId(HttpServletRequest request) {
        return request.getHeader(CORRELATION_ID);
    }

    public HttpServletRequest setCorrelationId(HttpServletRequest request, String correlationId) {
        return new HttpServletRequestWrapper(request) {
            @Override
            public String getHeader(String name) {
                if (name != null && name.equalsIgnoreCase(CORRELATION_ID)) {
                    return correlationId;
                }
                return super.getHeader(name);
            }

            @Override
            public Enumeration<String> getHeaderNames() {
                Set<String> set = new HashSet<>();
                Enumeration<String> enumeration = super.getHeaderNames();
                while (enumeration.hasMoreElements()) {
                    set.add(enumeration.nextElement());
                }
                set.add(CORRELATION_ID);
                return Collections.enumeration(set);
            }
        };
    }

    public HandlerFilterFunction<ServerResponse, ServerResponse> correlationIdFilter() {
        return (request, next) -> {
            HttpServletRequest servletRequest = request.servletRequest();
            String correlationId = getCorrelationId(servletRequest);

            if (correlationId == null || correlationId.isEmpty()) {
                correlationId = UUID.randomUUID().toString();
            }

            final String finalCorrelationId = correlationId;

            var modifiedRequest = org.springframework.web.servlet.function.ServerRequest.from(request)
                    .headers(headers -> headers.set(CORRELATION_ID, finalCorrelationId))
                    .build();

            ServerResponse response = next.handle(modifiedRequest);
            response.headers().add(CORRELATION_ID, finalCorrelationId);
            return response;
        };
    }
}
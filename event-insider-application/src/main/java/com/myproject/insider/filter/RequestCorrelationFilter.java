package com.myproject.insider.filter;

import java.io.IOException;
import java.util.UUID;

import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.myproject.insider.util.RequestCorrelation;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RequestCorrelationFilter extends OncePerRequestFilter {

    public static final String HEADER_REQUEST_ID = "X-Request-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String incoming = request.getHeader(HEADER_REQUEST_ID);
        String requestId = StringUtils.hasText(incoming) ? incoming.trim() : UUID.randomUUID().toString();
        request.setAttribute(RequestCorrelation.REQUEST_ID_ATTRIBUTE, requestId);
        response.setHeader(HEADER_REQUEST_ID, requestId);
        filterChain.doFilter(request, response);
    }
}
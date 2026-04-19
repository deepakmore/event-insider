package com.myproject.insider.filter;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.myproject.insider.util.RequestCorrelation;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RequestCorrelationFilter extends OncePerRequestFilter {

    public static final String HEADER_REQUEST_ID = "X-Request-Id";
    public static final String HEADER_USER_ID = "X-User-Id";
    private static final String USER_ID_QUERY_PARAM = "userId";
    private static final String MDC_KEY_REQUEST_ID = "requestId";
    private static final String MDC_KEY_USER_ID = "userId";
    private static final String ANONYMOUS_USER = "anonymous";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String incoming = request.getHeader(HEADER_REQUEST_ID);
        String requestId = StringUtils.hasText(incoming) ? incoming.trim() : UUID.randomUUID().toString();
        String userId = resolveUserId(request);
        request.setAttribute(RequestCorrelation.REQUEST_ID_ATTRIBUTE, requestId);
        response.setHeader(HEADER_REQUEST_ID, requestId);
        MDC.put(MDC_KEY_REQUEST_ID, requestId);
        MDC.put(MDC_KEY_USER_ID, userId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY_REQUEST_ID);
            MDC.remove(MDC_KEY_USER_ID);
        }
    }

    private static String resolveUserId(HttpServletRequest request) {
        String headerUserId = request.getHeader(HEADER_USER_ID);
        if (StringUtils.hasText(headerUserId)) {
            return headerUserId.trim();
        }
        String paramUserId = request.getParameter(USER_ID_QUERY_PARAM);
        if (StringUtils.hasText(paramUserId)) {
            return paramUserId.trim();
        }
        return ANONYMOUS_USER;
    }
}
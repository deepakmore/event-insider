package com.myproject.insider.filter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.insider.dto.ApiResponse;
import com.myproject.insider.util.HttpRequestCurlBuilder;
import com.myproject.insider.util.RequestCorrelation;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthFilter extends OncePerRequestFilter {

    private static final String SSO_TOKEN_HEADER = "X-SSO-Token";

    private final ObjectMapper objectMapper;

    private final boolean requireSsoToken;
    private final String expectedSsoToken;

    public AuthFilter(ObjectMapper objectMapper, boolean requireSsoToken, String expectedSsoToken) {
        this.objectMapper = objectMapper;
        this.requireSsoToken = requireSsoToken;
        this.expectedSsoToken = expectedSsoToken;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!requireSsoToken) {
            return true;
        }
        String uri = request.getRequestURI();
        if (uri.startsWith("/actuator")) {
            return true;
        }
        if ("/api/v1/payments/webhook".equals(uri)) {
            return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String providedToken = request.getHeader(SSO_TOKEN_HEADER);
        if (!StringUtils.hasText(providedToken) || !providedToken.equals(expectedSsoToken)) {
            writeUnauthorized(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void writeUnauthorized(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("curl", HttpRequestCurlBuilder.toCurl(request));
        ApiResponse<Map<String, Object>> body = ApiResponse.error(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Missing or empty " + SSO_TOKEN_HEADER + " header",
                RequestCorrelation.getRequestId(),
                data);
        if (body.getMeta() != null && body.getMeta().getResponseId() != null) {
            response.setHeader("X-Response-Id", body.getMeta().getResponseId());
        }
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
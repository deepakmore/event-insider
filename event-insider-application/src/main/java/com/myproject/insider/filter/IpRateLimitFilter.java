package com.myproject.insider.filter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.insider.dto.ApiResponse;
import com.myproject.insider.service.IpRateLimitService;
import com.myproject.insider.util.RequestCorrelation;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IpRateLimitFilter extends OncePerRequestFilter {

    private static final String BOOKING_CREATE_PATH = "/api/v1/bookings";
    private static final String HTTP_POST = "POST";
    private static final String HEADER_FORWARDED_FOR = "X-Forwarded-For";
    private static final String HEADER_REAL_IP = "X-Real-IP";
    private static final String HEADER_RETRY_AFTER = "Retry-After";

    private final ObjectMapper objectMapper;
    private final IpRateLimitService rateLimitService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !HTTP_POST.equalsIgnoreCase(request.getMethod())
                || !BOOKING_CREATE_PATH.equals(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String clientIp = resolveClientIp(request);
        IpRateLimitService.RateLimitDecision decision = rateLimitService.evaluate(clientIp);
        if (decision.allowed()) {
            filterChain.doFilter(request, response);
            return;
        }
        if (decision.blacklisted()) {
            writeError(response, HttpServletResponse.SC_FORBIDDEN,
                    "Access denied: IP is blacklisted.",
                    decision.ip(),
                    0);
            return;
        }
        response.setHeader(HEADER_RETRY_AFTER, String.valueOf(decision.retryAfterSeconds()));
        writeError(response, 429,
                "Too many requests from this IP. Try again after 15 minutes.",
                decision.ip(),
                decision.retryAfterSeconds());
    }

    private void writeError(HttpServletResponse response, int httpStatus, String message, String ip, long retryAfterSeconds)
            throws IOException {
        response.setStatus(httpStatus);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("ip", ip);
        if (retryAfterSeconds > 0) {
            data.put("retryAfterSeconds", retryAfterSeconds);
        }
        ApiResponse<Map<String, Object>> body = ApiResponse.error(httpStatus, message, RequestCorrelation.getRequestId(), data);
        response.setHeader("X-Response-Id", body.getMeta().getResponseId());
        objectMapper.writeValue(response.getOutputStream(), body);
    }

    private static String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader(HEADER_FORWARDED_FOR);
        if (StringUtils.hasText(forwarded)) {
            int comma = forwarded.indexOf(',');
            return (comma >= 0 ? forwarded.substring(0, comma) : forwarded).trim();
        }
        String realIp = request.getHeader(HEADER_REAL_IP);
        if (StringUtils.hasText(realIp)) {
            return realIp.trim();
        }
        String remoteAddr = request.getRemoteAddr();
        return StringUtils.hasText(remoteAddr) ? remoteAddr.trim() : "unknown";
    }
}

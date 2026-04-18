package com.myproject.insider.filter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.myproject.insider.util.HttpRequestCurlBuilder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HttpExchangeLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(HttpExchangeLoggingFilter.class);

    private static final int DEFAULT_CACHE_LIMIT = 16 * 1024;
    private static final int LOG_BODY_CHAR_MAX = 8_192;

    private static final Set<String> SENSITIVE_HEADERS = Set.of("authorization", "cookie", "set-cookie");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!(request instanceof ContentCachingRequestWrapper)) {
            request = new ContentCachingRequestWrapper(request, DEFAULT_CACHE_LIMIT);
        }
        if (!(response instanceof ContentCachingResponseWrapper)) {
            response = new ContentCachingResponseWrapper(response);
        }
        long start = System.nanoTime();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = (System.nanoTime() - start) / 1_000_000L;
            logExchange((ContentCachingRequestWrapper) request, (ContentCachingResponseWrapper) response, durationMs);
            ((ContentCachingResponseWrapper) response).copyBodyToResponse();
        }
    }

    private void logExchange(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response,
                             long durationMs) {
        String requestBody = readBodyForLog(request.getContentAsByteArray(), request.getCharacterEncoding(),
                request.getContentType());
        String responseBody = readBodyForLog(response.getContentAsByteArray(), response.getCharacterEncoding(),
                response.getContentType());

        Map<String, List<String>> requestHeaders = headerMap(request);
        Map<String, List<String>> responseHeaders = new LinkedHashMap<>();
        for (String name : response.getHeaderNames()) {
            responseHeaders.put(name, new ArrayList<>(response.getHeaders(name)));
        }

        if (log.isInfoEnabled()) {
            log.info(
                    "HTTP request method={} uri={}{} durationMs={} requestHeaders={} requestBody={} responseStatus={} responseHeaders={} responseBody={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    StringUtils.hasText(request.getQueryString()) ? "?" + request.getQueryString() : "",
                    durationMs,
                    maskHeaders(requestHeaders),
                    truncate(requestBody),
                    response.getStatus(),
                    maskHeaders(responseHeaders),
                    truncate(responseBody));
        }
        if (log.isInfoEnabled() && isErrorStatus(response.getStatus())) {
            log.info("HTTP curl {}", HttpRequestCurlBuilder.toCurl(request));
        }
    }

    private static boolean isErrorStatus(int status) {
        return status >= 400;
    }

    private static Map<String, List<String>> headerMap(HttpServletRequest request) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        Enumeration<String> names = request.getHeaderNames();
        while (names != null && names.hasMoreElements()) {
            String name = names.nextElement();
            Enumeration<String> values = request.getHeaders(name);
            List<String> list = new ArrayList<>();
            while (values != null && values.hasMoreElements()) {
                list.add(values.nextElement());
            }
            map.put(name, list);
        }
        return map;
    }

    private static Map<String, List<String>> maskHeaders(Map<String, List<String>> headers) {
        return headers.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> {
                            if (SENSITIVE_HEADERS.contains(e.getKey().toLowerCase(Locale.ROOT))) {
                                return List.of("***");
                            }
                            return List.copyOf(e.getValue());
                        },
                        (a, b) -> b,
                        LinkedHashMap::new));
    }

    private static String readBodyForLog(byte[] raw, String encoding, String contentType) {
        if (raw == null || raw.length == 0) {
            return "";
        }
        if (contentType != null && contentType.toLowerCase(Locale.ROOT).startsWith("multipart/")) {
            return "[multipart body omitted]";
        }
        if (contentType != null) {
            try {
                MediaType mediaType = MediaType.parseMediaType(contentType);
                if (mediaType.includes(MediaType.APPLICATION_OCTET_STREAM)) {
                    return "[octet-stream " + raw.length + " bytes]";
                }
            } catch (Exception ignored) {
                // fall through to string decode
            }
        }
        Charset charset = StandardCharsets.UTF_8;
        if (StringUtils.hasText(encoding)) {
            try {
                charset = Charset.forName(encoding);
            } catch (Exception ignored) {
                // keep UTF-8
            }
        }
        return new String(raw, charset);
    }

    private static String truncate(String s) {
        if (s == null) {
            return "";
        }
        if (s.length() <= LOG_BODY_CHAR_MAX) {
            return s;
        }
        return s.substring(0, LOG_BODY_CHAR_MAX) + "…[truncated " + (s.length() - LOG_BODY_CHAR_MAX) + " chars]";
    }
}
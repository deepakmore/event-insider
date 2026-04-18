package com.myproject.insider.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

import jakarta.servlet.http.HttpServletRequest;

public final class HttpRequestCurlBuilder {

    private static final int BODY_MAX = 16 * 1024;

    private static final Set<String> SENSITIVE_HEADERS = Set.of("authorization", "cookie", "set-cookie");

    private HttpRequestCurlBuilder() {
    }

    public static String toCurl(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        Map<String, List<String>> headers = headerMap(request);
        String body = readBodyForCurl(request);
        return buildCurl(request, headers, body);
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

    private static String readBodyForCurl(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper wrapper) {
            byte[] raw = wrapper.getContentAsByteArray();
            return readBodyString(raw, request.getCharacterEncoding(), request.getContentType());
        }
        return "";
    }

    private static String readBodyString(byte[] raw, String encoding, String contentType) {
        if (raw == null || raw.length == 0) {
            return "";
        }
        if (raw.length > BODY_MAX) {
            return new String(raw, 0, BODY_MAX, StandardCharsets.UTF_8) + "…[truncated]";
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
                // fall through
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

    private static String buildCurl(HttpServletRequest request, Map<String, List<String>> headers, String body) {
        String url = request.getRequestURL().toString();
        if (StringUtils.hasText(request.getQueryString())) {
            url += "?" + request.getQueryString();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("curl -X ").append(request.getMethod());
        sb.append(" '").append(escapeSingleQuoted(url)).append("'");

        List<String> headerNames = new ArrayList<>(headers.keySet());
        headerNames.sort(String.CASE_INSENSITIVE_ORDER);

        for (String name : headerNames) {
            if ("content-length".equalsIgnoreCase(name)) {
                continue;
            }
            for (String value : headers.get(name)) {
                String display = SENSITIVE_HEADERS.contains(name.toLowerCase(Locale.ROOT)) ? "***" : value;
                sb.append(" -H '").append(escapeSingleQuoted(name)).append(": ")
                        .append(escapeSingleQuoted(display)).append("'");
            }
        }

        if (StringUtils.hasText(body) && !"[multipart body omitted]".equals(body)
                && !body.startsWith("[octet-stream ")) {
            sb.append(" --data-raw '").append(escapeSingleQuoted(body)).append("'");
        }

        return sb.toString();
    }

    private static String escapeSingleQuoted(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("'", "'\\''");
    }
}
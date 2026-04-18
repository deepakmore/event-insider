package com.myproject.insider.advice;

import com.myproject.insider.util.RequestCorrelation;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.myproject.insider.dto.ApiResponse;

@ControllerAdvice(basePackages = "com.myproject.insider.controller")
public class ApiResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {
        if (body instanceof ApiResponse) {
            addResponseIdHeader(response, (ApiResponse<?>) body);
            return body;
        }
        if (body instanceof Resource) {
            return body;
        }
        if (body == null && response instanceof ServletServerHttpResponse servletResponse
                && servletResponse.getServletResponse().getStatus() == 204) {
            return null;
        }
        String requestId = RequestCorrelation.getRequestId();
        int httpStatus = resolveHttpStatus(response);
        ApiResponse<Object> envelope = ApiResponse.success(body, requestId, httpStatus);
        addResponseIdHeader(response, envelope);
        return envelope;
    }

    private static int resolveHttpStatus(ServerHttpResponse response) {
        if (response instanceof ServletServerHttpResponse servletResponse) {
            int status = servletResponse.getServletResponse().getStatus();
            if (status > 0) {
                return status;
            }
        }
        return 200;
    }

    private static void addResponseIdHeader(ServerHttpResponse response, ApiResponse<?> envelope) {
        if (envelope.getMeta() != null && envelope.getMeta().getResponseId() != null) {
            response.getHeaders().add("X-Response-Id", envelope.getMeta().getResponseId());
        }
    }
}
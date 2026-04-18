package com.myproject.insider.advice;

import java.util.LinkedHashMap;
import java.util.Map;

import com.myproject.insider.exception.ApiBadRequestException;
import com.myproject.insider.exception.ApiConflictException;
import com.myproject.insider.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.myproject.insider.dto.ApiResponse;
import com.myproject.insider.util.HttpRequestCurlBuilder;
import com.myproject.insider.util.RequestCorrelation;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleNotFound(ResourceNotFoundException ex,
                                                                           HttpServletRequest request) {
        Map<String, Object> data = curlOnlyData(request);
        ApiResponse<Map<String, Object>> body = ApiResponse.error(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                RequestCorrelation.getRequestId(),
                data);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .header("X-Response-Id", body.getMeta().getResponseId())
                .body(body);
    }

    @ExceptionHandler(ApiConflictException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleConflict(ApiConflictException ex,
                                                                           HttpServletRequest request) {
        Map<String, Object> data = curlOnlyData(request);
        ApiResponse<Map<String, Object>> body = ApiResponse.error(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                RequestCorrelation.getRequestId(),
                data);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .header("X-Response-Id", body.getMeta().getResponseId())
                .body(body);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleOptimisticLock(ObjectOptimisticLockingFailureException ex,
                                                                                 HttpServletRequest request) {
        Map<String, Object> data = curlOnlyData(request);
        ApiResponse<Map<String, Object>> body = ApiResponse.error(
                HttpStatus.CONFLICT.value(),
                "Concurrent update conflict",
                RequestCorrelation.getRequestId(),
                data);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .header("X-Response-Id", body.getMeta().getResponseId())
                .body(body);
    }

    @ExceptionHandler(ApiBadRequestException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleApiBadRequest(ApiBadRequestException ex,
                                                                                HttpServletRequest request) {
        Map<String, Object> data = curlOnlyData(request);
        ApiResponse<Map<String, Object>> body = ApiResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                RequestCorrelation.getRequestId(),
                data);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header("X-Response-Id", body.getMeta().getResponseId())
                .body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleValidation(MethodArgumentNotValidException ex,
                                                                             HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalid"));
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("errors", errors);
        data.put("curl", HttpRequestCurlBuilder.toCurl(request));
        ApiResponse<Map<String, Object>> body = ApiResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                RequestCorrelation.getRequestId(),
                data);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header("X-Response-Id", body.getMeta().getResponseId())
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleUnhandled(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception", ex);
        Map<String, Object> data = curlOnlyData(request);
        ApiResponse<Map<String, Object>> body = ApiResponse.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error",
                RequestCorrelation.getRequestId(),
                data);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("X-Response-Id", body.getMeta().getResponseId())
                .body(body);
    }

    private static Map<String, Object> curlOnlyData(HttpServletRequest request) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("curl", HttpRequestCurlBuilder.toCurl(request));
        return data;
    }
}
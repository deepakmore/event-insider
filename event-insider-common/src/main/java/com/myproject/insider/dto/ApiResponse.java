package com.myproject.insider.dto;

import java.util.UUID;

import com.myproject.insider.constants.ApplicationServiceCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    private ApiMeta meta;
    private T data;

    public static <T> ApiResponse<T> success(T data, String requestId) {
        return success(data, requestId, 200);
    }

    public static <T> ApiResponse<T> success(T data, String requestId, int httpStatus) {
        String responseId = UUID.randomUUID().toString();
        String code = ApplicationServiceCode.fromHttpStatus(httpStatus);
        ApiMeta meta = ApiMeta.builder()
                .code(code)
                .status("success")
                .message("")
                .requestId(nullToEmpty(requestId))
                .responseId(responseId)
                .build();
        return ApiResponse.<T>builder().meta(meta).data(data).build();
    }

    public static <T> ApiResponse<T> error(int httpStatus, String message, String requestId, T data) {
        return error(ApplicationServiceCode.fromHttpStatus(httpStatus), message, requestId, data);
    }

    public static <T> ApiResponse<T> error(String code, String message, String requestId, T data) {
        String responseId = UUID.randomUUID().toString();
        ApiMeta meta = ApiMeta.builder()
                .code(nullToEmpty(code))
                .status("error")
                .message(nullToEmpty(message))
                .requestId(nullToEmpty(requestId))
                .responseId(responseId)
                .build();
        return ApiResponse.<T>builder().meta(meta).data(data).build();
    }

    private static String nullToEmpty(String s) {
        return s != null ? s : "";
    }
}
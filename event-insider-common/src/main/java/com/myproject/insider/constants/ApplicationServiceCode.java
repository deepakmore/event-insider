package com.myproject.insider.constants;

public final class ApplicationServiceCode {

    public static final String SUCCESS_200 = "EVENT_INSIDER_SERVICE_SUCCESS_200";
    public static final String SUCCESS_201 = "EVENT_INSIDER_SERVICE_SUCCESS_201";
    public static final String ERROR_400 = "EVENT_INSIDER_SERVICE_ERROR_400";
    public static final String ERROR_401 = "EVENT_INSIDER_SERVICE_ERROR_401";
    public static final String ERROR_404 = "EVENT_INSIDER_SERVICE_ERROR_404";
    public static final String ERROR_409 = "EVENT_INSIDER_SERVICE_ERROR_409";
    public static final String ERROR_500 = "EVENT_INSIDER_SERVICE_ERROR_500";

    private ApplicationServiceCode() {
    }

    public static String fromHttpStatus(int statusCode) {
        if (statusCode == 200) {
            return SUCCESS_200;
        }
        if (statusCode == 201) {
            return SUCCESS_201;
        }
        if (statusCode == 400) {
            return ERROR_400;
        }
        if (statusCode == 401) {
            return ERROR_401;
        }
        if (statusCode == 404) {
            return ERROR_404;
        }
        if (statusCode == 409) {
            return ERROR_409;
        }
        if (statusCode == 500) {
            return ERROR_500;
        }
        if (statusCode >= 200 && statusCode < 300) {
            return SUCCESS_200;
        }
        if (statusCode >= 400 && statusCode < 500) {
            return ERROR_400;
        }
        if (statusCode >= 500) {
            return ERROR_500;
        }
        return SUCCESS_200;
    }
}
package com.myproject.insider.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Object id) {
        super("%s not found for id: %s".formatted(resource, id));
    }
}
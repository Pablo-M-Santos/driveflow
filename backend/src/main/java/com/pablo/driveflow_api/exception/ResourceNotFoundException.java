package com.pablo.driveflow_api.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends CustomException {

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND.value());
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s não encontrado(a) com %s: %s", resourceName, fieldName, fieldValue),
                HttpStatus.NOT_FOUND.value());
    }
}


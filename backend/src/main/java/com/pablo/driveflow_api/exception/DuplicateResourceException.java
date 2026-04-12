package com.pablo.driveflow_api.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends CustomException {

    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT.value());
    }

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s com %s '%s' já existe", resourceName, fieldName, fieldValue),
                HttpStatus.CONFLICT.value());
    }
}


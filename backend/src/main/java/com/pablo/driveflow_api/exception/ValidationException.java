package com.pablo.driveflow_api.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends CustomException {

    public ValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST.value());
    }
}


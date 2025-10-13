package com.ivanrl.yaet.domain;

import lombok.Getter;

@Getter
public class ValidationError extends RuntimeException {

    public ValidationError(String message) {
        super(message);
    }
}

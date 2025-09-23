package com.ivanrl.yaet.yaetApp;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

    private final String title;

    public BadRequestException(String message) {
        super(message);
        this.title = "Invalid request";
    }
}

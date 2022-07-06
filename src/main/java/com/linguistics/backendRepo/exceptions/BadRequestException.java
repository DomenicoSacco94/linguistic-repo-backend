package com.linguistics.backendRepo.exceptions;

public class BadRequestException extends ControllerException {
    public BadRequestException(String message) {
        super(message);
    }
}

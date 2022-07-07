package com.linguistics.backendRepo.exceptions;

public class InternalServerException extends ControllerException {
    public InternalServerException(String message) {
        super(message);
    }
}

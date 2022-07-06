package com.linguistics.backendRepo.exceptions;

public class DuplicateBookException extends ControllerException {
    public DuplicateBookException(String message) {
        super(message);
    }
}

package com.linguistics.backendRepo.exceptions;

public class DuplicateUsernameException extends ControllerException {
    public DuplicateUsernameException(String message) {
        super(message);
    }
}

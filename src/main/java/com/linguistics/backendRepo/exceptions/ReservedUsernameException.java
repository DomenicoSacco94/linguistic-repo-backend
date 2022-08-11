package com.linguistics.backendRepo.exceptions;

public class ReservedUsernameException extends ControllerException {
    public ReservedUsernameException(String message) {
        super(message);
    }
}

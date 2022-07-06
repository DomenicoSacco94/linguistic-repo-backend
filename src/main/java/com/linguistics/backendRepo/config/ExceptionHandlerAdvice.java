package com.linguistics.backendRepo.config;

import com.linguistics.backendRepo.exceptions.BadRequestException;
import com.linguistics.backendRepo.exceptions.BookNotFoundException;
import com.linguistics.backendRepo.exceptions.ControllerException;
import com.linguistics.backendRepo.exceptions.DuplicateBookException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity handleException(BadRequestException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity handleException(BookNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(DuplicateBookException.class)
    public ResponseEntity handleException(DuplicateBookException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(e.getMessage());
    }
}

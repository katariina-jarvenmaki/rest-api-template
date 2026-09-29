package com.restapitemplate.restapitemplate.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

/**
 * Turns exceptions from any controller into RFC 9457 "problem detail" JSON, the standard error body format
 *
 * @author KatariinaJ
 * @version 2026-09-29
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Make the 404s thrown by the service layer to carry a reason
    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatus(ResponseStatusException ex) {

        ProblemDetail body = ProblemDetail.forStatus(ex.getStatusCode());
        body.setTitle(ex.getReason());
        return body;
    }

    // A failed @Valid check becomes a 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {

        ProblemDetail body = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        body.setTitle("Validation failed");
        body.setProperty("invalidField",
            ex.getBindingResult().getFieldError().getField());
        return body;
    }
}
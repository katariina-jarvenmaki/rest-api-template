package com.restapitemplate.restapitemplate.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Turns exceptions from any controller into RFC 9457 "problem detail" JSON, the standard error body format
 *
 * @author KatariinaJ
 * @version 2026-10-02
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

    // Malformed JSON becomes a 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleUnreadableBody(HttpMessageNotReadableException ex) {

        ProblemDetail body = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        body.setTitle("Malformed request body");
        return body;
    }

    // A route with no controller mapping becomes a 404
    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleUnknownRoute(NoResourceFoundException ex) {

        ProblemDetail body = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        body.setTitle("Route not found");
        return body;
    }

    // A path variable that cannot become the controller's type becomes a 400
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        ProblemDetail body = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        body.setTitle("Invalid path variable");
        return body;
    }

    // A method the route does not support becomes a 405
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail handleUnsupportedMethod(HttpRequestMethodNotSupportedException ex) {

        ProblemDetail body = ProblemDetail.forStatus(HttpStatus.METHOD_NOT_ALLOWED);
        body.setTitle("Method not allowed");
        return body;
    }

    // A body in a media type that no converter could read -> 415
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ProblemDetail handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {

        ProblemDetail body = ProblemDetail.forStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        body.setTitle("Unsupported media type");
        return body;
    }

    // An Accept header with no response can satisfy a 406
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ProblemDetail handleNotAcceptable(HttpMediaTypeNotAcceptableException ex) {

        ProblemDetail body = ProblemDetail.forStatus(HttpStatus.NOT_ACCEPTABLE);
        body.setTitle("Not acceptable");
        return body;
    }
}
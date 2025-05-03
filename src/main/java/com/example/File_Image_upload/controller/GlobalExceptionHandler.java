package com.example.File_Image_upload.controller;

import com.example.File_Image_upload.exceptions.InvalidExcelException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Handle method argument validation errors
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request) {

        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return buildResponseEntity(new ApiError(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST,
            "VALIDATION_FAILED",
            "Request validation failed",
            errors
        ));
    }

    // Handle constraint violation exceptions
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(
        ConstraintViolationException ex) {

        Map<String, Object> errors = ex.getConstraintViolations().stream()
            .collect(Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                violation -> Map.of(
                    "message", violation.getMessage(),
                    "invalidValue", violation.getInvalidValue(),
                    "constraint", violation.getConstraintDescriptor()
                        .getAnnotation()
                        .annotationType()
                        .getSimpleName()
                )
            ));

        return buildResponseEntity(new ApiError(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST,
            "CONSTRAINT_VIOLATION",
            "Parameter validation failed",
            errors
        ));
    }

    // Handle resource not found exceptions
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNoSuchElementException(
        NoSuchElementException ex) {

        return buildResponseEntity(new ApiError(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND,
            "RESOURCE_NOT_FOUND",
            ex.getMessage()
        ));
    }

    // Handle invalid JSON format
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request) {

        return buildResponseEntity(new ApiError(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST,
            "INVALID_JSON",
            "Malformed JSON request"
        ));
    }


    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<String> handleMultipartError(MultipartException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("File upload error: " + ex.getMessage());
    }
    // Handle access denied exceptions
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(
        AccessDeniedException ex) {

        return buildResponseEntity(new ApiError(
            LocalDateTime.now(),
            HttpStatus.FORBIDDEN,
            "ACCESS_DENIED",
            "Insufficient permissions"
        ));
    }

    // Handle missing endpoints
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
        NoHandlerFoundException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request) {

        return buildResponseEntity(new ApiError(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND,
            "ENDPOINT_NOT_FOUND",
            String.format("Endpoint %s %s not found",
                ex.getHttpMethod(), ex.getRequestURL())
        ));
    }
    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("File processing error: " + ex.getMessage());
    }

    @ExceptionHandler(InvalidExcelException.class)
    public ResponseEntity<String> handleInvalidExcel(InvalidExcelException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ex.getMessage());
    }

    // Generic exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex) {
        return buildResponseEntity(new ApiError(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_ERROR",
            "An unexpected error occurred",
            Map.of("error", ex.getMessage())
        ));
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    // Error response structure
    public static class ApiError {
        private final LocalDateTime timestamp;
        private final HttpStatus status;
        private final String errorCode;
        private final String message;
        private final Map<String, Object> details;

        public ApiError(LocalDateTime timestamp, HttpStatus status,
                        String errorCode, String message) {
            this(timestamp, status, errorCode, message, new HashMap<>());
        }

        public ApiError(LocalDateTime timestamp, HttpStatus status,
                        String errorCode, String message, Map<String, Object> details) {
            this.timestamp = timestamp;
            this.status = status;
            this.errorCode = errorCode;
            this.message = message;
            this.details = details;
        }

        // Getters
        public LocalDateTime getTimestamp() { return timestamp; }
        public HttpStatus getStatus() { return status; }
        public String getErrorCode() { return errorCode; }
        public String getMessage() { return message; }
        public Map<String, Object> getDetails() { return details; }
    }
}

package com.ict300.P04.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserExists(UserAlreadyExistsException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.CONFLICT, ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.NOT_FOUND, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FirebaseAuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthError(FirebaseAuthenticationException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.UNAUTHORIZED, ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ProductExistException.class)
    public ResponseEntity<ApiError> handleProductExist(ProductExistException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.CONFLICT , ex.getMessage()) , HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public  ResponseEntity<ApiError> handleProductNotExist(ProductNotFoundException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.NOT_FOUND, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public  ResponseEntity<ApiError> handleUserNotExist(UserNotFoundException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.NOT_FOUND, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AppException.class)
    public  ResponseEntity<ApiError> handleApp(AppException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


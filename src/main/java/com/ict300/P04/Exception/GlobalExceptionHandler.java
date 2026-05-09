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
        return new ResponseEntity<>(new ApiError(HttpStatus.CONFLICT, ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiError> handleProductNotExist(ProductNotFoundException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.NOT_FOUND, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotExist(UserNotFoundException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.NOT_FOUND, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiError> handleApp(AppException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidPhoneNumberException.class)
    public ResponseEntity<ApiError> handleInvalidPhoneNumber(InvalidPhoneNumberException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedPaymentMethodException.class)
    public ResponseEntity<ApiError> handleUnsupportedPaymentMethod(UnsupportedPaymentMethodException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmptyOrderListException.class)
    public ResponseEntity<ApiError> handleEmptyOrderListMethod(EmptyOrderListException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiError> handleOrderNotFoundMethod(OrderNotFoundException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.NOT_FOUND, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CommandeAlreadyCancelledException.class)
    public ResponseEntity<ApiError> handleCommandCancelled(CommandeAlreadyCancelledException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.CONFLICT, ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(OtpCodeNotFoundException.class)
    public ResponseEntity<ApiError> handleOtpCodeNotFound(OtpCodeNotFoundException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.NOT_FOUND, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OtpCodeExpiredException.class)
    public ResponseEntity<ApiError> handleOtpCodeExpired(OtpCodeExpiredException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidOtpCodeException.class)
    public ResponseEntity<ApiError> handleInvalidOtpCode(InvalidOtpCodeException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    // --- ICI C'EST CORRIGÉ --- On utilise ApiError
    @ExceptionHandler(MaxAttemptsExceededException.class)
    public ResponseEntity<ApiError> handleMaxAttemptsExceeded(MaxAttemptsExceededException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.LOCKED, ex.getMessage()), HttpStatus.LOCKED);
    }

    @ExceptionHandler(OrderAlreadyFulfilledException.class)
    public ResponseEntity<ApiError> handleOrderAlreadyFulfilled(OrderAlreadyFulfilledException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.CONFLICT, ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(OrderNotPaidException.class)
    public ResponseEntity<ApiError> handleOrderNotPaid(OrderNotPaidException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.CONFLICT, ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(StoreMismatchException.class)
    public ResponseEntity<ApiError> handleStoreMismatch(StoreMismatchException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.FORBIDDEN, ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(QuincaillerieNotFoundException.class)
    public ResponseEntity<ApiError> handleQuincaillerie(QuincaillerieNotFoundException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.NOT_FOUND, ex.getMessage()), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobalException(Exception ex) {

        ex.printStackTrace();

        return new ResponseEntity<>(
                new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur inattendue s'est produite sur le serveur."),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
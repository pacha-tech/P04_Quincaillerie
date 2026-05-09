package com.ict300.P04.Exception;

public class InvalidOtpCodeException extends RuntimeException {
    public InvalidOtpCodeException(String message) {
        super(message);
    }
}

package com.ict300.P04.Exception;

public class OtpCodeExpiredException extends RuntimeException {
    public OtpCodeExpiredException(String message) {
        super(message);
    }
}

package com.ict300.P04.Exception;

public class OrderAlreadyFulfilledException extends RuntimeException {
    public OrderAlreadyFulfilledException(String message) {
        super(message);
    }
}

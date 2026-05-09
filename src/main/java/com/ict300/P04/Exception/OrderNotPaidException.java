package com.ict300.P04.Exception;

public class OrderNotPaidException extends RuntimeException {
    public OrderNotPaidException(String message) {
        super(message);
    }
}

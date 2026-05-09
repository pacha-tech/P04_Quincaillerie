package com.ict300.P04.Exception;

public class EmptyOrderListException extends RuntimeException {
    public EmptyOrderListException(String message) {
        super(message);
    }
}

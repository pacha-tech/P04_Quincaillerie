package com.ict300.P04.Exception;

public class CommandeAlreadyCancelledException extends RuntimeException{
    public CommandeAlreadyCancelledException(String message) {
        super(message);
    }
}

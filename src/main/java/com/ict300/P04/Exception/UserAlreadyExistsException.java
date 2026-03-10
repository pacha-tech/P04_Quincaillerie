package com.ict300.P04.Exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("L'utilisateur avec l'email " + email + " existe déjà.");
    }
}


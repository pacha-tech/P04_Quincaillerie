package com.ict300.P04.Utilitaires;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;

import java.util.UUID;

@Component
public class GenerateID {


    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();

    public String GenerateUserID() {
        StringBuilder sb = new StringBuilder("USER");
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public String GenerateQuincaillerieID() {
        StringBuilder sb = new StringBuilder("QUIN");
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}

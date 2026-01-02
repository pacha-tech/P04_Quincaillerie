package com.ict300.P04.Utilitaires;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GenerateID {

    public String GenerateUserID(){
        String prefixe = "USER";
        return prefixe + UUID.randomUUID().toString().substring(1,6);
    }
}

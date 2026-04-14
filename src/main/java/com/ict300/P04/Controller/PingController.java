package com.ict300.P04.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @GetMapping("/quincaillerie/ping")
    public String ping() {
        return "Pong! Le serveur est réveillé.";
    }
}

package com.ict300.P04.Controller.paiement;

import com.ict300.P04.Service.paiement.PaiementSseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/quincaillerie/paiementNotification")
public class PaiementSseController {
    @Autowired
    private PaiementSseService paiementSseService;

    @GetMapping(value = "/stream/{transactionId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter suivrePaiement(@PathVariable String transactionId) {
        log.info("Demande de suivi en temps réel reçue pour la transaction : {}", transactionId);
        return paiementSseService.abonnerAuPaiement(transactionId);
    }
}

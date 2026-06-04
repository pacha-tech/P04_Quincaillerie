package com.ict300.P04.Service.paiement;

import com.ict300.P04.Utilitaires.StatutPaiement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class PaiementSseService {

    private final Map<String, SseEmitter> paiementEmitters = new ConcurrentHashMap<>();

    public SseEmitter abonnerAuPaiement(String transactionId) {

        SseEmitter emitter = new SseEmitter(120_000L);

        Runnable nettoyerEmitter = () -> {
            paiementEmitters.remove(transactionId);
            log.debug("Émetteur SSE supprimé pour la transaction : {}", transactionId);
        };

        emitter.onCompletion(nettoyerEmitter);
        emitter.onTimeout(nettoyerEmitter);
        emitter.onError((e) -> nettoyerEmitter.run());

        try {
            emitter.send(SseEmitter.event()
                    .name("statut-paiement")
                    .data("{\"status\":\"PENDING\",\"message\":\"En attente du code PIN...\"}", MediaType.APPLICATION_JSON));
        } catch (IOException e) {
            log.error("Impossible d'envoyer l'état initial pour {}", transactionId, e);
            emitter.complete();
            return emitter;
        }

        paiementEmitters.put(transactionId, emitter);
        log.info("Client connecté en SSE pour suivre la transaction : {}", transactionId);

        return emitter;
    }

    public void notifierChangementStatut(String transactionId, StatutPaiement statut) {
        SseEmitter emitter = paiementEmitters.get(transactionId);

        if (emitter == null) {
            log.debug("Aucun client en attente de flux SSE pour la transaction : {}", transactionId);
            return;
        }

        try {
            log.info("Envoi du statut [{}] en temps réel pour la transaction : {}", statut, transactionId);

            emitter.send(SseEmitter.event()
                    .name("statut-paiement")
                    .data("{\"status\":\"" + statut + "\"}", MediaType.APPLICATION_JSON));

            if (!StatutPaiement.PENDING.equals(statut)) {
                emitter.complete();
                paiementEmitters.remove(transactionId);
            }

        } catch (IOException e) {
            log.warn("Échec de l'envoi du statut, fermeture forcée pour la transaction : {}", transactionId);
            emitter.complete();
            paiementEmitters.remove(transactionId);
        }
    }
}

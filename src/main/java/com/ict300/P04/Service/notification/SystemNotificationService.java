package com.ict300.P04.Service.notification;

import com.ict300.P04.DTO.notification.SystemNotificationDTO;
import com.ict300.P04.Entite.Price;
import com.ict300.P04.Entite.SystemNotification;
import com.ict300.P04.repository.interfaces.systemNotification.SystemNotificationInterface;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class SystemNotificationService {

    @Autowired
    private SystemNotificationInterface systemNotificationInterface;

    private final Map<String, List<SseEmitter>> vendeursEmitters = new ConcurrentHashMap<>();

    public SseEmitter addEmitter(String idQuincaillerie) {
        // Timeout de 24h
        SseEmitter emitter = new SseEmitter(24 * 60 * 60 * 1000L);

        Runnable removeEmitter = () -> {
            List<SseEmitter> emitters = vendeursEmitters.get(idQuincaillerie);
            if (emitters != null) {
                emitters.remove(emitter);
                if (emitters.isEmpty()) vendeursEmitters.remove(idQuincaillerie);
            }
            log.debug("Émetteur SSE supprimé pour la quincaillerie : {}", idQuincaillerie);
        };

        emitter.onCompletion(removeEmitter);
        emitter.onTimeout(removeEmitter);
        emitter.onError((e) -> removeEmitter.run());

        // 🔥 ASTUCE DE PRODUCTION : Envoi d'un ping immédiat pour valider la connexion auprès du client/navigateur
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("Connexion établie avec succès", MediaType.TEXT_PLAIN));
        } catch (IOException e) {
            log.error("Impossible d'envoyer l'événement initial de connexion", e);
            emitter.complete();
            return emitter;
        }

        vendeursEmitters.computeIfAbsent(idQuincaillerie, k -> new CopyOnWriteArrayList<>()).add(emitter);
        log.info("Nouvel émetteur SSE enregistré pour la quincaillerie : {}", idQuincaillerie);

        return emitter;
    }

    public void broadcastNotifications(String idQuincaillerie, List<SystemNotificationDTO> notifications) {
        List<SseEmitter> emitters = vendeursEmitters.getOrDefault(idQuincaillerie, Collections.emptyList());

        if (emitters.isEmpty()) {
            log.debug("Aucun vendeur connecté en SSE pour la quincaillerie : {}", idQuincaillerie);
            return;
        }

        log.info("Envoi de {} notification(s) en temps réel à la quincaillerie : {}", notifications.size(), idQuincaillerie);

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("nouvelles-notifications") // ⚠️ On change le nom de l'événement pour être générique
                        .data(notifications, MediaType.APPLICATION_JSON));
            } catch (IOException e) {
                log.warn("Échec de l'envoi de la notification, fermeture forcée de l'émetteur.");
                emitter.complete();
            }
        }
    }

    @Scheduled(fixedRate = 15000)
    public void sendHeartbeat() {

        vendeursEmitters.forEach((vendeurId, emitters) -> {
            emitters.removeIf(emitter -> {
                try {
                    emitter.send(SseEmitter.event()
                            .name("ping")
                            .data("keep-alive", MediaType.TEXT_PLAIN));
                    return false;
                } catch (Exception e) {
                    log.debug("Le heartbeat a échoué, suppression d'un émetteur pour {}", vendeurId);
                }
                return true;
            });
        });

        vendeursEmitters.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    public List<SystemNotificationDTO> getNotificationHistory(String idQuincaillerie) {
        return systemNotificationInterface.findByQuincaillerieIdQuincaillerieOrderByCreatedAtDesc(idQuincaillerie)
                .stream().map(notification -> new SystemNotificationDTO(
                        notification.getIdSystemNotification(),
                        notification.getMessage(),
                        notification.getType(),
                        notification.getTargetId(),
                        notification.getIsRead(),
                        notification.getCreatedAt()
                )).toList();
    }

    @Transactional
    public void markAllNotificationsAsRead(String idQuincaillerie) {
        systemNotificationInterface.markAllAsRead(idQuincaillerie);
    }
}
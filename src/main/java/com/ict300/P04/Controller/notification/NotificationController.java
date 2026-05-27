package com.ict300.P04.Controller.notification;

import com.ict300.P04.Controller.CheckController;
import com.ict300.P04.DTO.notification.SystemNotificationDTO;
import com.ict300.P04.Service.notification.SystemNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/quincaillerie/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final SystemNotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> claims = (Map<String, Object>) authentication.getDetails();

        if (claims == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Aucun détail d'authentification disponible");
        }

        String quincaillerieId = (String) claims.get("quincaillerieId");

        if (quincaillerieId == null || quincaillerieId.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "quincaillerieId manquant dans les claims");
        }

        log.info("Demande d'abonnement aux notifications SSE reçue pour la quincaillerie : {}", quincaillerieId);
        return notificationService.addEmitter(quincaillerieId);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getHistory(Authentication authentication) {
        var error = CheckController.validateQuincaillerieAuthentication(authentication);
        if (error.isPresent()) return error.get();

        String idQuincaillerie = CheckController.getQuincaillerieId(authentication);

        List<SystemNotificationDTO> history = notificationService.getNotificationHistory(idQuincaillerie);
        return ResponseEntity.ok(history);
    }

    @PatchMapping("/read")
    public ResponseEntity<?> markAsRead(Authentication authentication) {
        var error = CheckController.validateQuincaillerieAuthentication(authentication);
        if (error.isPresent()) return error.get();

        String idQuincaillerie = CheckController.getQuincaillerieId(authentication);

        notificationService.markAllNotificationsAsRead(idQuincaillerie);
        return ResponseEntity.ok().body("{\"message\": \"Toutes les notifications ont été marquées comme lues\"}");
    }
}
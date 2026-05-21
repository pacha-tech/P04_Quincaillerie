package com.ict300.P04.Controller.notification;

import com.ict300.P04.Controller.CheckController;
import com.ict300.P04.Service.notification.SystemNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/quincaillerie/notifications")
@RequiredArgsConstructor // <-- Remplaçant du @Autowired pour injection propre par constructeur
public class NotificationController {

    private final SystemNotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(Authentication authentication) {

        var error = CheckController.validateQuincaillerieAuthentication(authentication);

        String quincaillerieId = CheckController.getQuincaillerieId(authentication);


        log.info("Demande d'abonnement aux notifications SSE reçue pour la quincaillerie : {}", quincaillerieId);
        return notificationService.addEmitter(quincaillerieId);
    }
}
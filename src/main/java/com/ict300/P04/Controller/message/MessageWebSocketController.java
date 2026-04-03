package com.ict300.P04.Controller.message;

import com.ict300.P04.DTO.message.response.MessageDTO;
import com.ict300.P04.DTO.message.request.IncomingMessageDTO;
import com.ict300.P04.Service.message.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Slf4j
@Controller
public class MessageWebSocketController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/chat")
    public void processMessageFromFlutter(@Payload IncomingMessageDTO incomingMessageDTO, Authentication authentication) {
        log.info("CETTE FOIS CA DOIT S'AFFICHER ! Data: " + incomingMessageDTO);
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                log.info("Pas Authentifier ");

                return;
            }

            String uid = authentication.getName();



            MessageDTO savedMessage = messageService.enregistrerMessage(incomingMessageDTO, uid);

            messagingTemplate.convertAndSendToUser(
                    savedMessage.getIdReceiver(),
                    "/prive/messages",
                    savedMessage
            );


            messagingTemplate.convertAndSendToUser(
                    uid,
                    "/prive/messages",
                    savedMessage
            );


            messagingTemplate.convertAndSend(
                    "/canal/conversation/" + savedMessage.getIdConversation(),
                    savedMessage
            );

        } catch (Exception e) {
            if (authentication != null) {
                envoyerErreur(authentication.getName(), "Erreur lors de l'envoi : " + e.getMessage());
                System.out.println(e.getMessage());
            }
        }
    }

    private void envoyerErreur(String user, String message) {
        messagingTemplate.convertAndSendToUser(
                user,
                "/prive/errors", // Cohérent avec ton prefixe personnalisé
                Map.of("error", message, "timestamp", System.currentTimeMillis())
        );
    }
}

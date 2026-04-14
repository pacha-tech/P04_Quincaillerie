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
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                log.info("Pas Authentifier ");

                return;
            }

            String uid = authentication.getName();

            MessageDTO savedMessage = messageService.enregistrerMessage(incomingMessageDTO, uid);

            System.out.println(savedMessage);
            /*
            messagingTemplate.convertAndSendToUser(
                    savedMessage.getIdReceiver(),
                    "/prive/messages",
                    savedMessage
            );
            log.info("📢 DIFFUSION DU MESSAGE SUR LE CANAL : /prive/messages" + savedMessage.getIdReceiver());

            messagingTemplate.convertAndSendToUser(
                    uid,
                    "/prive/messages",
                    savedMessage
            );
            log.info("📢 DIFFUSION DU MESSAGE SUR LE CANAL : /prive/messgae" + uid);


            messagingTemplate.convertAndSend(
                    "/canal/conversation/" + savedMessage.getIdConversation(),
                    savedMessage
            );
            log.info("📢 DIFFUSION DU MESSAGE SUR LE CANAL : /canal/conversation/" + savedMessage.getIdConversation());
            */
                /**/
            messagingTemplate.convertAndSend(
                    "/canal/notifications/" + savedMessage.getIdReceiver(),
                    savedMessage
            );
            log.info("📢 NOTIFICATION ENVOYÉE SUR : /canal/notifications/" + savedMessage.getIdReceiver());

            // On s'envoie la notification à nous-même (pour mettre à jour notre propre liste)
            messagingTemplate.convertAndSend(
                    "/canal/notifications/" + uid,
                    savedMessage
            );
            log.info("📢 NOTIFICATION ENVOYÉE SUR : /canal/notifications/" + uid);

            // La diffusion dans le chat (tu ne touches à rien ici)
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

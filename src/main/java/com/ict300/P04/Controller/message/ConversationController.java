package com.ict300.P04.Controller.message;

import com.ict300.P04.DTO.message.response.ConversationDTO;
import com.ict300.P04.Exception.ApiError;
import com.ict300.P04.Exception.ProductNotFoundException;
import com.ict300.P04.Exception.UserNotFoundException;
import com.ict300.P04.Service.message.ConversationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/quincaillerie/conversation")
@Tag(name = "MessagePanier", description = "Gestion des message")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;


    @GetMapping("/getAllConversationByUser")
    public ResponseEntity<?> getAllConversationByUser(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiError(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié"));
        }

        String uid = authentication.getName(); // C'est l'UID Firebase du client

        @SuppressWarnings("unchecked")
        Map<String, Object> claims = (Map<String, Object>) authentication.getDetails();
        String role = (String) claims.get("role");

        if (role == null || role.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiError(HttpStatus.FORBIDDEN, "Rôle manquant"));
        }

        try {
            List<ConversationDTO> response;

            // LOGIQUE DISTINCTE SELON LE RÔLE
            if ("vendeur".equalsIgnoreCase(role)) {
                // Pour le vendeur, on récupère l'ID de sa quincaillerie stocké dans les claims
                String idQuincaillerie = (String) claims.get("quincaillerieId");
                response = conversationService.getAllByQuincaillerie(idQuincaillerie);
            } else {
                // Pour le client (rôle "client" ou autre), on utilise son UID
                response = conversationService.getAllByClient(uid);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération"));
        }
    }
}

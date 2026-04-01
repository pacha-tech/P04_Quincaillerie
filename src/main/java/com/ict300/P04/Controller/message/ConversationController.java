package com.ict300.P04.Controller.message;

import com.ict300.P04.DTO.message.response.ConversationDTO;
import com.ict300.P04.Exception.ApiError;
import com.ict300.P04.Exception.ProductNotFoundException;
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiError(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié"));
        }

        String uid = authentication.getName();

        @SuppressWarnings("unchecked")
        Map<String, Object> claims = (Map<String, Object>) authentication.getDetails();
        if (claims == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiError(HttpStatus.FORBIDDEN, "Aucun détail d'authentification disponible"));
        }

        String role = (String) claims.get("role");
        if (role == null || role.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiError(HttpStatus.FORBIDDEN, "Pas de Role vous devez etre connectez"));
        }


        try {
            List<ConversationDTO> response = conversationService.getAllConversationByUser(uid);
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("Erreur: "+e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de la verification de l'existance du produit dans le panier"));
        }
    }
}

package com.ict300.P04.Controller.message;

import com.ict300.P04.DTO.message.response.MessageDTO;
import com.ict300.P04.Exception.ApiError;
import com.ict300.P04.Exception.ProductNotFoundException;
import com.ict300.P04.Service.message.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/quincaillerie/message")
@Tag(name = "Message", description = "Gestion des message")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @GetMapping("/getAllMessage")
    public ResponseEntity<?> getAllMessageByConversation(@RequestParam String idConversation , Authentication authentication) {

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
            List<MessageDTO> response = messageService.getAllMessageByConversation(idConversation);
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("Erreur: "+e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de la verification de l'existance du produit dans le panier"));
        }
    }

    @PostMapping("/markRead")
    public ResponseEntity<?> readConfirmation(@RequestBody List<String> idMessages, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiError(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié"));
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
                    .body(new ApiError(HttpStatus.FORBIDDEN, "Pas de rôle, vous devez être connecté"));
        }


        String idLecteur = uid;

        if ("VENDEUR".equalsIgnoreCase(role)) {

            String quincaillerieId = (String) claims.get("quincaillerieId");

            if (quincaillerieId == null || quincaillerieId.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiError(HttpStatus.FORBIDDEN, "ID Quincaillerie introuvable dans le token"));
            }
            idLecteur = quincaillerieId;
        }

        try {

            messageService.readConfirmation(idMessages, idLecteur);

            return ResponseEntity.ok("Tous les messages sont marqués comme lus");

        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("Erreur: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de la mise à jour du statut de lecture des messages"));
        }
    }
}

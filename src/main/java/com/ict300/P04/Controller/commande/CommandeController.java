package com.ict300.P04.Controller.commande;

import com.ict300.P04.DTO.commande.response.CommandeDetailDTO;
import com.ict300.P04.DTO.commande.response.CommandeResponseDTO;
import com.ict300.P04.DTO.commande.response.getCommandeDTO;
import com.ict300.P04.Exception.ApiError;
import com.ict300.P04.Service.commmande.CommandeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/quincaillerie/commande")
@Tag(name = "ManageCommande", description = "Gestion des comandes")
public class CommandeController {

    @Autowired
    private CommandeService commandeService;


    @Operation(summary = "Creation d'une commande par un client connecte")
    @PostMapping("/pass")
    public ResponseEntity<?> passCommand(@RequestParam(required = false) String idQuincaillerie , Authentication authentication) {

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

        List<CommandeResponseDTO> responseDTO = commandeService.processCommande(idQuincaillerie,uid);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Recuperation de tout les commandes par utilisateur")
    @GetMapping("/getAllCommandesByUser")
    public ResponseEntity<?> getAllCommande(Authentication authentication) {

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

        List<getCommandeDTO> responseDTO = commandeService.getAllCommandeByUser(uid);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Recuperation des details d'une commande")
    @GetMapping("/details/{id}")
    public ResponseEntity<?> getDetailCommande(@PathVariable("id") String idCommande , Authentication authentication) {

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

        List<CommandeDetailDTO> responseDTO = commandeService.getDetailCommande(idCommande);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Recuperation des details d'une commande")
    @GetMapping("/getAllCommandesByQuincaillerie")
    public ResponseEntity<?> getCommandeByQuincaillerie(Authentication authentication) {

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

        String quincaillerieId = (String) claims.get("quincaillerieId");
        if (quincaillerieId == null || quincaillerieId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiError(HttpStatus.FORBIDDEN, "quincaillerieId manquant dans les claims"));
        }


        List<getCommandeDTO> responseDTO = commandeService.getCommandeByQuincaillerie(quincaillerieId);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Recuperation des details d'une commande")
    @PostMapping("/validate/{id}")
    public ResponseEntity<?> validateCommandeByQuincaillerie( @PathVariable("id") String idCommande ,Authentication authentication) {

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

        String quincaillerieId = (String) claims.get("quincaillerieId");
        if (quincaillerieId == null || quincaillerieId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiError(HttpStatus.FORBIDDEN, "quincaillerieId manquant dans les claims"));
        }


        commandeService.validateCommandeByQuincaillerie(idCommande , quincaillerieId);
        return ResponseEntity.ok("Commande valide avec succes");
    }

    @Operation(summary = "Recuperation des details d'une commande")
    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancelCommandeByQuincaillerie( @PathVariable("id") String idCommande ,Authentication authentication) {

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

        String quincaillerieId = (String) claims.get("quincaillerieId");
        if (quincaillerieId == null || quincaillerieId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiError(HttpStatus.FORBIDDEN, "quincaillerieId manquant dans les claims"));
        }


        commandeService.cancelCommandeByQuincaillerie(idCommande , quincaillerieId);
        return ResponseEntity.ok("Commande valide avec succes");
    }
}

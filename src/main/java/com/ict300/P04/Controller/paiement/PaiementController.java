package com.ict300.P04.Controller.paiement;


import com.ict300.P04.DTO.paiement.request.PaiementRequestDTO;
import com.ict300.P04.DTO.paiement.response.PaiementResponseDTO;
import com.ict300.P04.Exception.ApiError;
import com.ict300.P04.Service.paiement.PaiementService;
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
@RequestMapping("/quincaillerie/paiement")
@Tag(name = "ManagePaiement", description = "Gestion des paiements")
public class PaiementController {

    @Autowired
    private PaiementService paymentService;


    @Operation(summary = "Paiement d'une commande par un client connecte")
    @PostMapping("/pay")
    public ResponseEntity<?> simulatePayment(@RequestBody PaiementRequestDTO request , Authentication authentication) {

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

        PaiementResponseDTO responseDTO = paymentService.processPayment(request , uid);
        return ResponseEntity.ok(responseDTO);

    }
}

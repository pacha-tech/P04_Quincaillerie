package com.ict300.P04.Controller.quincaillerie;

import com.ict300.P04.DTO.quincaillerie.request.RegisterQuincaillerieDTO;
import com.ict300.P04.DTO.quincaillerie.response.QuincaillerieDetailsDTO;
import com.ict300.P04.Exception.ApiError;
import com.ict300.P04.Service.quincaillerie.QuincaillerieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/quincaillerie/quincaillerie")
@Tag(name = "ManageQuincaillerie", description = "Gestion des quincaillerie")
public class QuincaillerieController {
    @Autowired
    private QuincaillerieService quincaillerieService;

    @Operation(summary = "Details quincaillerie", description = "Récupère les détails via ID ou via l'utilisateur authentifié")
    @GetMapping("/details")
    public ResponseEntity<?> getDetails(@RequestParam(required = false) String idQuincaillerie, Authentication authentication) {

        String finalId = null;


        if (idQuincaillerie != null && !idQuincaillerie.trim().isEmpty()) {
            finalId = idQuincaillerie;
        } else if (authentication != null && authentication.isAuthenticated()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> claims = (Map<String, Object>) authentication.getDetails();

            if (claims != null) {
                String quincaillerieIdFromClaims = (String) claims.get("quincaillerieId");
                if (quincaillerieIdFromClaims != null && !quincaillerieIdFromClaims.trim().isEmpty()) {
                    finalId = quincaillerieIdFromClaims;
                }
            }
        }


        if (finalId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiError(HttpStatus.BAD_REQUEST, "Identifiant de quincaillerie manquant (paramètre ou session)"));
        }

        try {
            QuincaillerieDetailsDTO detailQuincaillerie = quincaillerieService.getInfoQuincaillerei(finalId);
            return ResponseEntity.ok(detailQuincaillerie);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError(HttpStatus.NOT_FOUND, "Quincaillerie non trouvée"));
        }
    }

    @PostMapping("/registerQuincaillerie")
    @Operation(summary = "Enregistrement d'une Quincaillerie")
    public ResponseEntity<?> registerUSer(@Valid @RequestBody RegisterQuincaillerieDTO registerQuincaillerieDTO , Authentication authentication) {

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


        quincaillerieService.registerQuincaillerie(registerQuincaillerieDTO , uid);
        return ResponseEntity.ok("Enregistrement de la quincaillerie reussis ");
    }
}

package com.ict300.P04.Controller.quincaillerie;

import com.ict300.P04.Controller.CheckController;
import com.ict300.P04.DTO.quincaillerie.request.RegisterQuincaillerieDTO;
import com.ict300.P04.DTO.quincaillerie.response.QuincaillerieDetailsDTO;
import com.ict300.P04.Exception.ApiError;
import com.ict300.P04.Service.quincaillerie.QuincaillerieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/quincaillerie/quincaillerie")
@RequiredArgsConstructor
@Tag(name = "ManageQuincaillerie", description = "Gestion des quincailleries")
public class QuincaillerieController {

    private final QuincaillerieService quincaillerieService;

    @GetMapping("/details")
    @Operation(summary = "Détails quincaillerie", description = "Récupère les détails via ID ou via la quincaillerie authentifiée")
    public ResponseEntity<?> getDetails(@RequestParam(required = false) String idQuincaillerie, Authentication authentication) {

        String finalId = null;

        if (idQuincaillerie != null && !idQuincaillerie.trim().isEmpty()) {
            finalId = idQuincaillerie;
        } else {
            var errorResponse = CheckController.validateQuincaillerieAuthentication(authentication);
            if (errorResponse.isPresent()) return errorResponse.get();

            finalId = CheckController.getQuincaillerieId(authentication);
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
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterQuincaillerieDTO registerQuincaillerieDTO, Authentication authentication) {

        var errorResponse = CheckController.validateBasicAuthentication(authentication);
        if (errorResponse.isPresent()) return errorResponse.get();

        String uid = CheckController.getUserId(authentication);


        @SuppressWarnings("unchecked")
        Map<String, Object> claims = (Map<String, Object>) authentication.getDetails();
        String role = (claims != null) ? (String) claims.get("role") : null;

        if (role == null || role.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiError(HttpStatus.FORBIDDEN, "Accès refusé : Rôle manquant, vous devez être connecté"));
        }

        quincaillerieService.registerQuincaillerie(registerQuincaillerieDTO, uid);
        return ResponseEntity.ok("Enregistrement de la quincaillerie réussi");
    }
}
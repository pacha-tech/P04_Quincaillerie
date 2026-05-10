package com.ict300.P04.Controller.statistique;

import com.ict300.P04.DTO.commande.response.CommandeStatsDTO;
import com.ict300.P04.Exception.ApiError;
import com.ict300.P04.Service.commmande.StatistiquesService;
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
@RequestMapping("/quincaillerie/stats")
@Tag(name = "ManageStatistiques", description = "Gestion des statistiques")
public class StatistiqueController {

    @Autowired
    private StatistiquesService statistiquesService;

    @GetMapping("/commandesChart")
    @Operation(summary = "Stats des Commandes", description = "Récupère les stats des comment en fonction des jours")
    public ResponseEntity<?> getCommandesStatsChart(@RequestParam int jours , Authentication authentication) {

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


        List<CommandeStatsDTO> stats = statistiquesService.getStatsForChart(quincaillerieId, jours);


        return ResponseEntity.ok(stats);
    }
}

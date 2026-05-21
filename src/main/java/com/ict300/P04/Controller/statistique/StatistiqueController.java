package com.ict300.P04.Controller.statistique;

import com.ict300.P04.Controller.CheckController;
import com.ict300.P04.DTO.commande.response.CommandeStatsDTO;
import com.ict300.P04.DTO.statistique.detailProduct.response.DernierMouvementDTO;
import com.ict300.P04.DTO.statistique.detailProduct.response.GraphiqueMouvementDTO;
import com.ict300.P04.DTO.statistique.detailProduct.response.IndicateursPerformanceDTO;
import com.ict300.P04.DTO.statistique.promotion.CampagnePromoStatsDTO;
import com.ict300.P04.DTO.vente.response.VentesStatsDTO;
import com.ict300.P04.Service.statistiques.ProduitStatsService;
import com.ict300.P04.Service.statistiques.PromotionStatsService;
import com.ict300.P04.Service.statistiques.StatistiquesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quincaillerie/stats")
@RequiredArgsConstructor
@Tag(name = "ManageStatistiques", description = "Gestion des statistiques")
public class StatistiqueController {

    private final StatistiquesService statistiquesService;
    private final ProduitStatsService produitStatsService;
    private final PromotionStatsService promotionStatsService;

    @GetMapping("/commandesChart")
    @Operation(summary = "Stats des Commandes", description = "Récupère les stats des commandes en fonction des jours")
    public ResponseEntity<?> getCommandesStatsChart(@RequestParam int jours, Authentication authentication) {
        var errorResponse = CheckController.validateQuincaillerieAuthentication(authentication);
        if (errorResponse.isPresent()) return errorResponse.get();

        String quincaillerieId = CheckController.getQuincaillerieId(authentication);
        List<CommandeStatsDTO> stats = statistiquesService.getStatsCommandForChart(quincaillerieId, jours);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/ventesChart")
    @Operation(summary = "Stats des Ventes", description = "Récupère les stats des ventes en fonction des jours")
    public ResponseEntity<?> getVentesStatsChart(@RequestParam int jours, Authentication authentication) {
        var errorResponse = CheckController.validateQuincaillerieAuthentication(authentication);
        if (errorResponse.isPresent()) return errorResponse.get();

        String quincaillerieId = CheckController.getQuincaillerieId(authentication);
        List<VentesStatsDTO> stats = statistiquesService.getStatsVentesForChart(quincaillerieId, jours);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/product/{id}/chart")
    @Operation(summary = "Graphique d'un produit", description = "Récupère les données d'entrées/sorties pour le graphique")
    public ResponseEntity<?> getProductStatsChart(@RequestParam int jours, @PathVariable("id") String idPrice, Authentication authentication) {
        var errorResponse = CheckController.validateQuincaillerieAuthentication(authentication);
        if (errorResponse.isPresent()) return errorResponse.get();

        List<GraphiqueMouvementDTO> stats = produitStatsService.getStatsProduct(idPrice, jours);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/product/{id}/kpis")
    @Operation(summary = "Indicateurs d'un produit", description = "Récupère les KPI (ventes, moyenne, pertes) du produit")
    public ResponseEntity<?> getIndicateurProduct(@RequestParam int jours, @PathVariable("id") String idPrice, Authentication authentication) {
        var errorResponse = CheckController.validateQuincaillerieAuthentication(authentication);
        if (errorResponse.isPresent()) return errorResponse.get();

        IndicateursPerformanceDTO stats = produitStatsService.getIndicateursProduct(idPrice, jours);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/product/{id}/dernier-mouvement")
    @Operation(summary = "Dernier mouvement", description = "Récupère la dernière activité de stock enregistrée pour ce produit")
    public ResponseEntity<?> getLastmouvement(@PathVariable("id") String idPrice, Authentication authentication) {
        var errorResponse = CheckController.validateQuincaillerieAuthentication(authentication);
        if (errorResponse.isPresent()) return errorResponse.get();

        DernierMouvementDTO lastMouvement = produitStatsService.getLastMouvement(idPrice);
        return ResponseEntity.ok(lastMouvement);
    }

    @GetMapping("/campagne/{idCampagne}/detail")
    @Operation(summary = "Détails d'une campagne", description = "Calcule les statistiques de performance d'une campagne promotionnelle")
    public ResponseEntity<?> getStatsPromo(@PathVariable String idCampagne, Authentication authentication) {
        var errorResponse = CheckController.validateQuincaillerieAuthentication(authentication);
        if (errorResponse.isPresent()) return errorResponse.get();

        CampagnePromoStatsDTO stats = promotionStatsService.calculerStatsCampagne(idCampagne);
        return ResponseEntity.ok(stats);
    }
}
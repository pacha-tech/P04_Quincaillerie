package com.ict300.P04.Controller.Promotion;

import com.ict300.P04.DTO.product.response.SearchProductDTO;
import com.ict300.P04.DTO.promotion.request.AddPromotionDTO;
import com.ict300.P04.DTO.promotion.response.PromotionDTO;
import com.ict300.P04.DTO.promotion.response.ProduitPromotionDTO;
import com.ict300.P04.Exception.*;
import com.ict300.P04.Service.promotion.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/quincaillerie/promotion")
@Tag(name = "ManagePromotion", description = "Gestion des promotion")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @Operation(summary = "Ajout d'une promotion pour un ensemble de produit")
    @PostMapping("/addPromotion")
    public ResponseEntity<?> addPromotion(@Valid @RequestBody AddPromotionDTO addPromotionDTO , Authentication authentication) {

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

        log.info("Ajout de la promo par UID: {} pour quincaillerie: {} sur les produits {} ", uid, quincaillerieId , addPromotionDTO.getIdsPrices());

        try {
            System.out.println("La promo est: "+addPromotionDTO);
            promotionService.addPromotion(addPromotionDTO , quincaillerieId);
            return ResponseEntity.ok(new ApiResponse(true, "Promotion ajouté avec succès"));
        } catch (ProductExistException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de l'ajout de la promo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de l'ajout"));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une promotion")
    public ResponseEntity<?> deletePromotion(@PathVariable("id") String idCampagnePromotion , Authentication authentication) {

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


        try {
            promotionService.deletePromotion(idCampagnePromotion , quincaillerieId);
            return ResponseEntity.ok(new ApiResponse(true, "Promotion supprimer avec succès"));
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la suppression de la promotion", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de suppression de la promo"));
        }
    }

    @GetMapping("/allProductOutPromotion")
    @Operation(summary = "Get tous les produit qui ne sont pas en promotion")
    public ResponseEntity<?> getAllProductOutPromotion(Authentication authentication) {

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


        try {
            List<ProduitPromotionDTO> produitPromotionDTOs = promotionService.getAllProduitOutPromotionByQuincaillerie(quincaillerieId);
            return ResponseEntity.ok(produitPromotionDTOs);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la recuperation des produits qui ne sont pas en promotion", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de suppression de la promo"));
        }
    }

    @GetMapping("/allProductInPromotion")
    @Operation(summary = "Get tous les produit qui sont en promotion")
    public ResponseEntity<?> getAllProductInPromotion() {

        try {
            List<SearchProductDTO> produitPromotionDTOs = promotionService.getAllProduitInPromotionGrouped();
            return ResponseEntity.ok(produitPromotionDTOs);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la recuperation des produits qui sont en promotion", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de la recuperation de tous les produits en promotion"));
        }
    }


    @GetMapping("/allPromotion")
    @Operation(summary = "Get tous les promotions d'une quincaillerie")
    public ResponseEntity<?> getAllPromotion(Authentication authentication) {

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


        try {
            List<PromotionDTO> produitPromotionDTOs = promotionService.getAllPromotionByQuincaillerie(quincaillerieId);
            return ResponseEntity.ok(produitPromotionDTOs);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la recuperation des promotions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de suppression de la promo"));
        }
    }

}

package com.ict300.P04.Controller.Promotion;

import com.ict300.P04.Controller.CheckController;
import com.ict300.P04.DTO.product.response.SearchProductDTO;
import com.ict300.P04.DTO.promotion.request.AddPromotionDTO;
import com.ict300.P04.DTO.promotion.response.PromotionDTO;
import com.ict300.P04.DTO.promotion.response.ProduitPromotionDTO;
import com.ict300.P04.Exception.*;
import com.ict300.P04.Service.promotion.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/quincaillerie/promotion")
@RequiredArgsConstructor
@Tag(name = "ManagePromotion", description = "Gestion des promotions")
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping("/addPromotion")
    @Operation(summary = "Ajout d'une promotion pour un ensemble de produits")
    public ResponseEntity<?> addPromotion(@Valid @RequestBody AddPromotionDTO addPromotionDTO, Authentication authentication) {

        var errorResponse = CheckController.validateQuincaillerieAuthentication(authentication);
        if (errorResponse.isPresent()) return errorResponse.get();

        String uid = CheckController.getUserId(authentication);
        String quincaillerieId = CheckController.getQuincaillerieId(authentication);

        log.info("Ajout de la promo par UID: {} pour quincaillerie: {} sur les produits {}", uid, quincaillerieId, addPromotionDTO.getIdsPrices());

        try {
            promotionService.addPromotion(addPromotionDTO, quincaillerieId);
            return ResponseEntity.ok(new ApiResponse(true, "Promotion ajoutée avec succès"));
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
    public ResponseEntity<?> deletePromotion(@PathVariable("id") String idCampagnePromotion, Authentication authentication) {

        var errorResponse = CheckController.validateQuincaillerieAuthentication(authentication);
        if (errorResponse.isPresent()) return errorResponse.get();

        String quincaillerieId = CheckController.getQuincaillerieId(authentication);

        try {
            promotionService.deletePromotion(idCampagnePromotion, quincaillerieId);
            return ResponseEntity.ok(new ApiResponse(true, "Promotion supprimée avec succès"));
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la suppression de la promotion", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de la suppression de la promo"));
        }
    }

    @GetMapping("/allProductOutPromotion")
    @Operation(summary = "Get tous les produits qui ne sont pas en promotion")
    public ResponseEntity<?> getAllProductOutPromotion(Authentication authentication) {

        var errorResponse = CheckController.validateQuincaillerieAuthentication(authentication);
        if (errorResponse.isPresent()) return errorResponse.get();

        String quincaillerieId = CheckController.getQuincaillerieId(authentication);

        try {
            List<ProduitPromotionDTO> produitPromotionDTOs = promotionService.getAllProduitOutPromotionByQuincaillerie(quincaillerieId);
            return ResponseEntity.ok(produitPromotionDTOs);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la récupération des produits hors promotion", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de la récupération des produits"));
        }
    }

    @GetMapping("/allProductInPromotion")
    @Operation(summary = "Get tous les produits qui sont en promotion, triés par proximité si le GPS est fourni")
    public ResponseEntity<?> getAllProductInPromotion(@RequestParam(required = false) Double latitude, @RequestParam(required = false) Double longitude) {
        // Cet endpoint est public (utilisé côté client pour voir la liste des promotions sans être connecté)
        try {
            List<SearchProductDTO> produitPromotionDTOs = promotionService.getAllProduitInPromotionGrouped(latitude, longitude);
            return ResponseEntity.ok(produitPromotionDTOs);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la récupération des produits en promotion", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de la récupération des produits en promotion"));
        }
    }

    @GetMapping("/allPromotion")
    @Operation(summary = "Get toutes les promotions d'une quincaillerie")
    public ResponseEntity<?> getAllPromotion(Authentication authentication) {

        var errorResponse = CheckController.validateQuincaillerieAuthentication(authentication);
        if (errorResponse.isPresent()) return errorResponse.get();

        String quincaillerieId = CheckController.getQuincaillerieId(authentication);

        try {
            List<PromotionDTO> promotionDTOs = promotionService.getAllPromotionByQuincaillerie(quincaillerieId);
            return ResponseEntity.ok(promotionDTOs);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la récupération des promotions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de la récupération des promotions"));
        }
    }
}
package com.ict300.P04.Controller.Panier;

import com.ict300.P04.Controller.CheckController;
import com.ict300.P04.DTO.product.response.ProductPanierDTO;
import com.ict300.P04.Exception.ApiError;
import com.ict300.P04.Exception.ApiResponse;
import com.ict300.P04.Exception.ProductExistException;
import com.ict300.P04.Exception.ProductNotFoundException;
import com.ict300.P04.Service.panier.PanierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/quincaillerie/panier")
@RequiredArgsConstructor
@Tag(name = "ManagePanier", description = "Gestion des paniers d'achat")
public class PanierController {

    private final PanierService panierService;

    @PostMapping("/addToPanier")
    @Operation(summary = "Ajout d'un produit dans le panier pour un utilisateur connecté")
    public ResponseEntity<?> addToPanier(@RequestParam String idPrice, Authentication authentication) {
        var errorResponse = validatePanierAccess(authentication);
        if (errorResponse.isPresent()) return errorResponse.get();

        String uid = CheckController.getUserId(authentication);

        try {
            panierService.addToPanier(idPrice, uid);
            return ResponseEntity.ok(new ApiResponse(true, "Produit ajouté avec succès au panier"));
        } catch (ProductExistException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur serveur lors de l'ajout au panier pour l'UID: {}", uid, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de l'ajout"));
        }
    }

    @DeleteMapping("/product/{id}")
    @Operation(summary = "Suppression d'un produit dans le panier par un utilisateur connecté")
    public ResponseEntity<?> deleteProductToPanier(@PathVariable("id") String idPrice, Authentication authentication) {
        var errorResponse = validatePanierAccess(authentication);
        if (errorResponse.isPresent()) return errorResponse.get();

        String uid = CheckController.getUserId(authentication);

        try {
            panierService.deleteProductInPanier(idPrice, uid);
            return ResponseEntity.ok(new ApiResponse(true, "Produit supprimé avec succès du panier"));
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur serveur lors de la suppression du produit {} du panier pour l'UID: {}", idPrice, uid, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de la suppression"));
        }
    }

    @GetMapping("/product/getQuantityInPanier")
    @Operation(summary = "Récupération de la quantité du produit dans le panier (qte = 0 si n'existe pas)")
    public ResponseEntity<?> getQuantityProductInPanier(@RequestParam String idPrice, Authentication authentication) {
        var errorResponse = validatePanierAccess(authentication);
        if (errorResponse.isPresent()) return errorResponse.get();

        String uid = CheckController.getUserId(authentication);

        try {
            int response = panierService.getQuantityProductInPanier(idPrice, uid);
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la vérification de la quantité du produit {} pour l'UID: {}", idPrice, uid, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de la vérification de la quantité"));
        }
    }

    @GetMapping("/getAllProductInPanier")
    @Operation(summary = "Obtenir tous les produits du panier")
    public ResponseEntity<?> getAllProductInPanierByUser(Authentication authentication) {
        var errorResponse = validatePanierAccess(authentication);
        if (errorResponse.isPresent()) return errorResponse.get();

        String uid = CheckController.getUserId(authentication);

        try {
            List<ProductPanierDTO> response = panierService.getAllProductInPanier(uid);
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du panier pour l'UID: {}", uid, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de la récupération du panier"));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer le panier d'une quincaillerie")
    public ResponseEntity<?> deletePanierByQuincaillerie(@PathVariable("id") String idQuincaillerie, Authentication authentication) {
        var errorResponse = validatePanierAccess(authentication);
        if (errorResponse.isPresent()) return errorResponse.get();

        String uid = CheckController.getUserId(authentication);

        try {
            panierService.deletePanier(idQuincaillerie, uid);
            return ResponseEntity.ok(new ApiResponse(true, "Panier supprimé avec succès"));
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du panier de la quincaillerie {} pour l'UID: {}", idQuincaillerie, uid, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de la suppression du panier"));
        }
    }

    @DeleteMapping("/all")
    @Operation(summary = "Supprimer tous les paniers d'un utilisateur")
    public ResponseEntity<?> deleteAllPaniersByUser(Authentication authentication) {
        var errorResponse = validatePanierAccess(authentication);
        if (errorResponse.isPresent()) return errorResponse.get();

        String uid = CheckController.getUserId(authentication);

        try {
            panierService.deleteAllPaniers(uid);
            return ResponseEntity.ok(new ApiResponse(true, "Tous les paniers ont été supprimés avec succès"));
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la suppression globale des paniers pour l'UID: {}", uid, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de la vidange des paniers"));
        }
    }

    @PostMapping("/addQuantityToPanier")
    @Operation(summary = "+1 au panier")
    public ResponseEntity<?> addToQuantityToPanier(@RequestParam String idPrice, Authentication authentication) {
        var errorResponse = validatePanierAccess(authentication);
        if (errorResponse.isPresent()) return errorResponse.get();

        String uid = CheckController.getUserId(authentication);

        try {
            panierService.addQuantityToPanier(idPrice, uid);
            return ResponseEntity.ok(new ApiResponse(true, "Quantité incrémentée (+1)"));
        } catch (ProductExistException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de l'incrémentation du produit {} pour l'UID: {}", idPrice, uid, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de la modification de la quantité"));
        }
    }

    @PostMapping("/removeQuantityToPanier")
    @Operation(summary = "-1 au panier")
    public ResponseEntity<?> removeToQuantityToPanier(@RequestParam String idPrice, Authentication authentication) {
        var errorResponse = validatePanierAccess(authentication);
        if (errorResponse.isPresent()) return errorResponse.get();

        String uid = CheckController.getUserId(authentication);

        try {
            panierService.removeQuantityToPanier(idPrice, uid);
            return ResponseEntity.ok(new ApiResponse(true, "Quantité décrémentée (-1)"));
        } catch (ProductExistException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la décrémentation du produit {} pour l'UID: {}", idPrice, uid, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de la soustraction de la quantité"));
        }
    }

    // --- HELPER DE VALIDATION INTERNE ---

    @SuppressWarnings("unchecked")
    private Optional<ResponseEntity<ApiError>> validatePanierAccess(Authentication authentication) {
        // 1. Validation de l'authentification via notre CheckController (Valable pour Client & Vendeur)
        var basicCheck = CheckController.validateBasicAuthentication(authentication);
        if (basicCheck.isPresent()) return basicCheck;

        // 2. Validation spécifique du rôle requise pour le panier
        Map<String, Object> claims = (Map<String, Object>) authentication.getDetails();
        String role = (claims != null) ? (String) claims.get("role") : null;

        if (role == null || role.trim().isEmpty()) {
            return Optional.of(ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiError(HttpStatus.FORBIDDEN, "Accès refusé : Rôle manquant, vous devez être connecté")));
        }

        return Optional.empty();
    }
}
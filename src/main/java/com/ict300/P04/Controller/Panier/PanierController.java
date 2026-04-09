package com.ict300.P04.Controller.Panier;

import com.ict300.P04.DTO.product.response.ProductPanierDTO;
import com.ict300.P04.Exception.ApiError;
import com.ict300.P04.Exception.ApiResponse;
import com.ict300.P04.Exception.ProductExistException;
import com.ict300.P04.Exception.ProductNotFoundException;
import com.ict300.P04.Service.panier.PanierService;
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
@RequestMapping("/quincaillerie/panier")
@Tag(name = "ManagePanier", description = "Gestion des panier")
public class PanierController {

    @Autowired
    private PanierService panierService;

    @Operation(summary = "Ajout d'un produit dans le panier pour un utilisateur connecté")
    @GetMapping("/addToPanier")
    public ResponseEntity<?> addToPanier(@RequestParam String idPrice , Authentication authentication) {

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
            panierService.addToPanier(idPrice , uid);
            return ResponseEntity.ok(new ApiResponse(true, "Produit ajouté avec succès au panier"));
        } catch (ProductExistException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("Erreur"+e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de l'ajout"));
        }
    }

    @Operation(summary = "Suppression d'un produit dans le panier par un utilisateur connecté")
    @DeleteMapping("/product/{id}")
    public ResponseEntity<?> deleteProductToPanier(@PathVariable("id") String idPrice , Authentication authentication) {

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
            panierService.deleteProductInPanier(idPrice , uid);
            return ResponseEntity.ok(new ApiResponse(true, "Produit supprimé avec succès du panier"));
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("Erreur"+e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de l'ajout"));
        }
    }

    @Operation(summary = "Recuperation de la quantite du produit dans le panier (qte = 0 si n'existe pas)")
    @GetMapping("/product/getQuantityInPanier")
    public ResponseEntity<?> getQuantityProductInPanier(@RequestParam String idPrice , Authentication authentication) {
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
            int response = panierService.getQuantityProductInPanier(idPrice , uid);
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("Erreur: "+e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de la verification de l'existance du produit dans le panier"));
        }
    }

    @Operation(summary = "Obtenir tous les produits du panier")
    @GetMapping("/getAllProductInPanier")
    public ResponseEntity<?> getAllProductInPanierByUser(Authentication authentication) {
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
            List<ProductPanierDTO> response = panierService.getAllProductInPanier(uid);
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("Erreur: "+e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de la verification de l'existance du produit dans le panier"));
        }
    }

    @Operation(summary = "Supprimer le panier d'une quincaillerie")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePanierByQuincaillerie(@PathVariable("id") String idQuincaillerie , Authentication authentication) {

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
            panierService.deletePanier(idQuincaillerie , uid);
            return ResponseEntity.ok(new ApiResponse(true, "Panier supprimé avec succès"));
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("Erreur"+e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de l'ajout"));
        }
    }

    @Operation(summary = "Supprimer tous les paniers d'un utilisateur")
    @DeleteMapping("/all")
    public ResponseEntity<?> deleteAllPaniersByUser(Authentication authentication) {

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
            panierService.deleteAllPaniers(uid);
            return ResponseEntity.ok(new ApiResponse(true, "Panier supprimé avec succès"));
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("Erreur"+e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de l'ajout"));
        }
    }

    @Operation(summary = "+1 au panier")
    @GetMapping("/addQuantityToPanier")
    public ResponseEntity<?> addToQuantityToPanier(@RequestParam String idPrice , Authentication authentication) {

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
            panierService.addQuantityToPanier(idPrice , uid);
            return ResponseEntity.ok(new ApiResponse(true, "+1 au panier"));
        } catch (ProductExistException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("Erreur"+e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de l'ajout de la quantite"));
        }
    }

    @Operation(summary = "-1 au panier")
    @GetMapping("/removeQuantityToPanier")
    public ResponseEntity<?> removeToQuantityToPanier(@RequestParam String idPrice , Authentication authentication) {

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
            panierService.removeQuantityToPanier(idPrice , uid);
            return ResponseEntity.ok(new ApiResponse(true, "-1 au panier"));
        } catch (ProductExistException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("Erreur"+e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de la soustraction de la quantite"));
        }
    }
}

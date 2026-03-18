package com.ict300.P04.Controller.products;

import com.ict300.P04.DTO.product.request.AddProductDTO;
import com.ict300.P04.DTO.product.request.UpdateProductDTO;
import com.ict300.P04.DTO.product.response.ProductStockDTO;
import com.ict300.P04.DTO.product.response.SearchProductDTO;
import com.ict300.P04.DTO.product.response.getProductSuggestionDTO;
import com.ict300.P04.DTO.recommadation.response.RecommendedProductDTO;
import com.ict300.P04.Exception.ApiError;
import com.ict300.P04.Exception.ApiResponse;
import com.ict300.P04.Exception.ProductExistException;
import com.ict300.P04.Exception.ProductNotFoundException;
import com.ict300.P04.Service.product.ProductService;
import com.ict300.P04.Service.recommadation.RecommandationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/quincaillerie/products")
@Tag(name = "ManageProducts", description = "Gestion des produits")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final RecommandationService recommandationService;

    @Operation(summary = "Rechercher des produits")
    @GetMapping("/search")
    public ResponseEntity<List<SearchProductDTO>> search(@RequestParam String name) {
        List<SearchProductDTO> results = productService.SearchProductByName(name);
        System.out.println(results);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Ajout d'un produit pour la quincaillerie de l'utilisateur connecté")
    @PostMapping("/addProduct")
    public ResponseEntity<?> addProduct(@Valid @RequestBody AddProductDTO addProductDTO, Authentication authentication) {

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

        log.info("Ajout produit par UID: {} pour quincaillerie: {}", uid, quincaillerieId);

        try {
            productService.AddProduct(addProductDTO, uid, quincaillerieId);
            return ResponseEntity.ok(new ApiResponse(true, "Produit ajouté avec succès"));
        } catch (ProductExistException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de l'ajout produit", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de l'ajout"));
        }
    }

    @Operation(summary = "Récupérer le stock de la quincaillerie")
    @GetMapping("/getStock")
    public ResponseEntity<List<ProductStockDTO>> getProductByQuincaillerie(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.emptyList());
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> claims = (Map<String, Object>) authentication.getDetails();
        if (claims == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
        }

        String qId = (String) claims.get("quincaillerieId");
        if (qId == null || qId.trim().isEmpty()) {
            log.error("Claim 'quincaillerieId' manquant pour UID: {}", authentication.getName());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
        }

        List<ProductStockDTO> stock = productService.getProductByQuincaillerie(qId);
        return ResponseEntity.ok(stock);
    }

    @Operation(summary = "Produits recommandés")
    @GetMapping("/recommendations")
    public ResponseEntity<List<RecommendedProductDTO>> getRecommendations(@RequestParam String idProduct, @RequestParam String idQuincaillerie) {
        List<RecommendedProductDTO> recommendations = recommandationService.getRecommendations(idProduct, idQuincaillerie);
        return ResponseEntity.ok(recommendations);
    }

    @Operation(summary = "Suggestions auto-complétion")
    @GetMapping("/suggestions")
    public ResponseEntity<List<getProductSuggestionDTO>> getSuggestions() {
        List<getProductSuggestionDTO> suggestions = productService.getAllSuggestions();
        return ResponseEntity.ok(suggestions);
    }

    @Operation(summary = "Mise à jour partielle d'un produit (PATCH)")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") String productId, @Valid @RequestBody UpdateProductDTO updateProductDTO, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiError(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié"));
        }


        String uid = authentication.getName();

        @SuppressWarnings("unchecked")
        Map<String, Object> claims = (Map<String, Object>) authentication.getDetails();
        if (claims == null) {
            System.out.println("Erreur 403");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiError(HttpStatus.FORBIDDEN, "Aucun détail d'authentification disponible"));
        }

        String quincaillerieId = (String) claims.get("quincaillerieId");
        if (quincaillerieId == null || quincaillerieId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiError(HttpStatus.FORBIDDEN, "quincaillerieId manquant dans les claims"));
        }

        log.info("Mise à jour produit ID: {} par UID: {} pour quincaillerie: {}", productId, uid, quincaillerieId);
        log.info(String.valueOf(updateProductDTO));

        try {
            productService.updateProduct(productId, updateProductDTO);

            // Succès
            return ResponseEntity.ok(new ApiResponse(true, "Produit mis à jour avec succès"));

        } catch (ProductNotFoundException e) {  // À créer si besoin (produit inexistant ou pas de cette quincaillerie)
            throw e;  // Ou return ResponseEntity.status(HttpStatus.NOT_FOUND).body(...)
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du produit "+productId+" Erreur: "+e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de la mise à jour"));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un produit")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") String idProduct , Authentication authentication) {

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
            productService.deleteProduct(idProduct , quincaillerieId);
            return ResponseEntity.ok(new ApiResponse(true, "Produit supprimer avec succès"));
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la suppression du produit", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de l'ajout"));
        }
    }
}
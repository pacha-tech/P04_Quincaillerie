package com.ict300.P04.Controller.products;

import com.ict300.P04.DTO.product.request.AddProductDTO;
import com.ict300.P04.DTO.product.request.UpdateProductDTO;
import com.ict300.P04.DTO.product.response.ProductStockDTO;
import com.ict300.P04.DTO.product.response.SearchProductDTO;
import com.ict300.P04.DTO.product.response.getProductSuggestionDTO;
import com.ict300.P04.DTO.recommadation.response.RecommendedProductDTO;
import com.ict300.P04.Exception.*;
import com.ict300.P04.Service.product.ProductService;
import com.ict300.P04.Service.recommadation.RecommandationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/quincaillerie/products")
@Tag(name = "ManageProducts", description = "Gestion des produits pour la plateforme Quincaillerie")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final RecommandationService recommandationService;

    // --- RECHERCHE ET CONSULTATION ---

    @Operation(summary = "Rechercher des produits par nom")
    @GetMapping("/search")
    public ResponseEntity<List<SearchProductDTO>> search(@RequestParam String name) {
        log.info("Recherche de produits avec le nom: {}", name);
        List<SearchProductDTO> results = productService.SearchProductByName(name);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Recuperer un produit par son idPrice")
    @GetMapping("/getProduct/{idPrice}")
    public ResponseEntity<SearchProductDTO> getProductSearchById(@PathVariable("idPrice") String idPrice) {
        log.info("Recuperation du produits avec l'id: {}", idPrice);
        SearchProductDTO results = productService.getProductById(idPrice);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Récupérer le stock complet de la quincaillerie")
    @GetMapping("/getStock")
    public ResponseEntity<?> getProductByQuincaillerie(Authentication authentication) {
        String qId = getQuincaillerieId(authentication);
        if (qId == null) return buildUnauthorizedResponse();
        List<ProductStockDTO> stock = productService.getStock(qId);
        return ResponseEntity.ok(stock);

    }

    @Operation(summary = "Suggestions pour l'auto-complétion")
    @GetMapping("/suggestions")
    public ResponseEntity<List<getProductSuggestionDTO>> getSuggestions() {
        return ResponseEntity.ok(productService.getAllSuggestions());
    }

    @Operation(summary = "Récupérer les produits recommandés")
    @GetMapping("/recommendations")
    public ResponseEntity<List<RecommendedProductDTO>> getRecommendations(@RequestParam String idProduct, @RequestParam String idQuincaillerie) {
        return ResponseEntity.ok(recommandationService.getRecommendations(idProduct, idQuincaillerie));
    }

    // --- OPÉRATIONS DE GESTION (CRUD) ---

    @Operation(summary = "Ajouter un nouveau produit avec image")
    @PostMapping(value = "/addProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProduct(@RequestPart("data") @Valid AddProductDTO addProductDTO, @RequestPart(value = "image", required = false) MultipartFile image, Authentication authentication) {

        String qId = getQuincaillerieId(authentication);
        if (qId == null) return buildUnauthorizedResponse();

        String uid = authentication.getName();
        log.info("Tentative d'ajout produit: {} par UID: {} (Image: {})", addProductDTO.getName(), uid, (image != null));

        try {
            productService.AddProduct(addProductDTO, image, uid, qId);
            return ResponseEntity.ok(new ApiResponse(true, "Produit ajouté avec succès"));
        } catch (ProductExistException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur serveur lors de l'ajout du produit", e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de l'ajout");
        }
    }

    @Operation(summary = "Mise à jour d'un produit existant (Données + Image)")
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(@PathVariable("id") String productId, @RequestPart("data") @Valid UpdateProductDTO updateProductDTO, @RequestPart(value = "image", required = false) MultipartFile image, Authentication authentication) {

        String qId = getQuincaillerieId(authentication);
        if (qId == null) return buildUnauthorizedResponse();

        log.info("Mise à jour produit ID: {} par UID: {} (Image: {})", productId, authentication.getName(), (image != null));

        try {
            productService.updateProduct(productId, updateProductDTO, image, qId);
            return ResponseEntity.ok(new ApiResponse(true, "Produit mis à jour avec succès"));
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du produit {}", productId, e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de la mise à jour");
        }
    }

    @Operation(summary = "Supprimer un produit")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") String idProduct, Authentication authentication) {

        String qId = getQuincaillerieId(authentication);
        if (qId == null) return buildUnauthorizedResponse();

        log.info("Suppression du produit ID: {} demandée par UID: {}", idProduct, authentication.getName());

        try {
            productService.deleteProduct(idProduct, qId);
            return ResponseEntity.ok(new ApiResponse(true, "Produit supprimé avec succès"));
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du produit {}", idProduct, e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur lors de la suppression");
        }
    }



    private String getQuincaillerieId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;

        Map<String, Object> claims = (Map<String, Object>) authentication.getDetails();
        if (claims == null) return null;

        return (String) claims.get("quincaillerieId");
    }

    private ResponseEntity<?> buildUnauthorizedResponse() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiError(HttpStatus.UNAUTHORIZED, "Accès non autorisé ou claims manquants"));
    }

    private ResponseEntity<?> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ApiError(status, message));
    }
}
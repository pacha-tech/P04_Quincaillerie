package com.ict300.P04.Controller.products;

import com.ict300.P04.DTO.product.request.AddProductDTO;
import com.ict300.P04.DTO.product.response.ProductStockDTO;
import com.ict300.P04.DTO.product.response.SearchProductDTO;
import com.ict300.P04.DTO.product.response.getProductDTO;
import com.ict300.P04.DTO.recommadation.response.RecommendedProductDTO;
import com.ict300.P04.Service.product.ProductService;
import com.ict300.P04.Service.recommadation.RecommandationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/quincaillerie/products")
@Tag(name = "ManageProducts", description = "Gestion des produits")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private RecommandationService recommandationService;

    @Operation(summary = "Rechercher des produits")
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String name) {
        List<SearchProductDTO> results = productService.SearchProductByName(name);
        return ResponseEntity.ok(results.isEmpty() ? Collections.emptyList() : results);
    }

    @Operation(summary = "L'ajout d'un produit")
    @PostMapping("/addProduct")
    public ResponseEntity<?> addProduct(@Valid @RequestBody AddProductDTO addProductDTO, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Utilisateur non authentifié");
        }

        // On récupère l'UID (qui est le principal dans ton FirebaseFilter)
        String uid = authentication.getName();

        productService.AddProduct(addProductDTO, uid);
        return ResponseEntity.ok("Produit ajouté avec succès par l'utilisateur " + uid);
    }

    @Operation(summary = "Récupérer le stock de la quincaillerie")
    @GetMapping("/getStock")
    public ResponseEntity<?> getProductByQuincaillerie(Authentication authentication) {
        if (authentication == null || authentication.getDetails() == null) {
            return ResponseEntity.status(401).body("Session invalide ou expirée");
        }

        // Récupération des claims via les details injectés par le FirebaseFilter
        Map<String, Object> claims = (Map<String, Object>) authentication.getDetails();
        String qId = (String) claims.get("quincaillerieId");

        if (qId == null) {
            System.err.println("❌ Claim 'quincaillerieId' manquant pour l'utilisateur: " + authentication.getName());
            return ResponseEntity.status(403).body("Erreur : Votre compte n'est lié à aucune quincaillerie.");
        }

        List<ProductStockDTO> stock = productService.getProductByQuincaillerie(qId);
        return ResponseEntity.ok(stock);
    }

    @Operation(summary = "Produits recommandés")
    @GetMapping("/recommendations")
    public ResponseEntity<List<RecommendedProductDTO>> getRecommendations(@RequestParam String idProduct, @RequestParam String idQuincaillerie) {
        return ResponseEntity.ok(recommandationService.getRecommendations(idProduct, idQuincaillerie));
    }

    @Operation(summary = "Suggestions auto-complétion")
    @GetMapping("/suggestions")
    public ResponseEntity<List<getProductDTO>> getSuggestions() {
        return ResponseEntity.ok(productService.getAllSuggestions());
    }
}
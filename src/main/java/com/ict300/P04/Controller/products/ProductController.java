package com.ict300.P04.Controller.products;

import com.ict300.P04.DTO.product.response.SearchProductDTO;
import com.ict300.P04.DTO.recommadation.response.RecommendedProductDTO;
import com.ict300.P04.DTO.suggestions.response.SuggestionSearchDTO;
import com.ict300.P04.Entite.Product;
import com.ict300.P04.Service.product.SearchProductService;
import com.ict300.P04.Service.recommadation.RecommandationService;
import com.ict300.P04.Service.suggestions.SuggestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/quincaillerie/products")
@Tag(name = "ManageProducts", description = "Gestion des produits")
public class ProductController {
    @Autowired
    SearchProductService searchProductService;

    @Autowired
    private RecommandationService recommandationService;

    @Autowired
    private SuggestionService suggestionService;

    @Operation(summary = "Rechercher des produits", description = "Recherche par nom avec la liste des prix et nom des quincailleries inclus")
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String name) {

        List<SearchProductDTO> results = searchProductService.SearchProductByName(name);
        if(results.isEmpty()){
            return ResponseEntity.ok(Collections.emptyList());
        }

        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Produits recommandés", description = "Recommande les produits de la meme category du produit cherché ")
    @GetMapping("/recommendations")
    public ResponseEntity<List<RecommendedProductDTO>> getRecommendations(@RequestParam String idProduct , @RequestParam String idQuincaillerie) {
        List<RecommendedProductDTO> recommandations = recommandationService.getRecommendations(idProduct , idQuincaillerie);
        return ResponseEntity.ok(recommandations);
    }

    @Operation(summary = "Produits suggerer lorsque l'utilisateur entre la recherche", description = "suggestion des mots pour l'auto-completion")
    @GetMapping("/suggestions")
    public ResponseEntity<List<SuggestionSearchDTO>> getSuggestions(@RequestParam String query) {
        if (query.length() < 2) {
            return ResponseEntity.ok(List.of());
        }

        List<SuggestionSearchDTO> suggestions = suggestionService.getAllSuggestions(query);

        return ResponseEntity.ok(suggestions);
    }
}

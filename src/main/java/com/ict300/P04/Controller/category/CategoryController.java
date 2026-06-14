package com.ict300.P04.Controller.category;

import com.ict300.P04.Controller.CheckController;
import com.ict300.P04.DTO.category.request.CategoryDTO;
import com.ict300.P04.DTO.category.request.HistoriqueNavigationRequestDTO;
import com.ict300.P04.DTO.category.response.AddCategoryDTO;
import com.ict300.P04.DTO.category.response.ProductInCategoryDTO;
import com.ict300.P04.Service.category.CategoryService;
import com.ict300.P04.Service.category.HistoriqueNavigationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/quincaillerie/category")
@Tag(name = "ManageCategory", description = "Gestion des categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private HistoriqueNavigationService historiqueNavigationService;

    @PostMapping("/addCategory")
    @Operation(summary = "Enregistrement d'une categorie")
    public ResponseEntity<?> addCategotyr(@Valid @RequestBody AddCategoryDTO addCategoryDTO) {
        categoryService.AddCategory(addCategoryDTO);
        return ResponseEntity.ok("Enregistrement Reussis de la categorie " + addCategoryDTO.getName());
    }

    @GetMapping("/allCategory")
    @Operation(summary = "Recuperation de toutes les categories")
    public ResponseEntity<?> allCategory() {
        List<CategoryDTO> category = categoryService.getAllCategory();
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Obtenir les produits d'une categorie")
    @GetMapping("/{id}")
    public ResponseEntity<?> search(@PathVariable("id") String idCategory , @RequestParam(required = false) Double latitude, @RequestParam(required = false) Double longitude,
                                    @RequestParam(required = false, defaultValue = "ville") String scope) {

        log.info("Recherche de produits - Nom: {}, Lat: {}, Lng: {}, Scope: {}", idCategory, latitude, longitude, scope);
        List<ProductInCategoryDTO> results = categoryService.getAllProductByCategory(idCategory , longitude , latitude , scope);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/track")
    @Operation(summary = "Inserer les donnees de navigation pour personaliser l'affichage")
    public ResponseEntity<?> trackAction(@RequestBody @Valid HistoriqueNavigationRequestDTO tracking , Authentication authentication) {
        var errorResponse = CheckController.validateBasicAuthentication(authentication);
        if (errorResponse.isPresent()) return errorResponse.get();

        String uid = CheckController.getUserId(authentication);

        historiqueNavigationService.saveHistoriqueNavigation(tracking , uid);

        return ResponseEntity.ok().build();
    }
}

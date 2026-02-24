package com.ict300.P04.Controller.category;

import com.ict300.P04.DTO.category.request.CategoryDTO;
import com.ict300.P04.DTO.category.response.AddCategoryDTO;
import com.ict300.P04.Service.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quincaillerie/category")
@Tag(name = "ManageCategory", description = "Gestion des categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

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
}

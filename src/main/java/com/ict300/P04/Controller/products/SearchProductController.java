package com.ict300.P04.Controller.products;

import com.ict300.P04.DTO.product.response.SearchProductDTO;
import com.ict300.P04.Service.product.SearchProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/quincaillerie/products")
@Tag(name = "SearchProduits", description = "Recherche et consultation des produits")
public class SearchProductController {
    @Autowired
    SearchProductService searchProductService;

    @Operation(summary = "Rechercher des produits", description = "Recherche par nom avec la liste des prix et nom des quincailleries inclus")
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String name) {

        List<SearchProductDTO> results = searchProductService.SearchProductByName(name);
        if(results.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body("Aucun produit trouvé pour "+name);
        }

        return ResponseEntity.ok(results);
    }
}

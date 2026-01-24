package com.ict300.P04.Controller.favorite;

import com.ict300.P04.DTO.favorite.request.AddFavoriteProductDTO;
import com.ict300.P04.DTO.favorite.request.AddFavoriteQuincaillerieDTO;
import com.ict300.P04.DTO.favorite.request.DeleteFavoriteProductDTO;
import com.ict300.P04.DTO.favorite.request.DeleteFavoriteQuincaillerieDTO;
import com.ict300.P04.Service.favorite.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quincaillerie/favorite")
@Tag(name = "ManageFavorite", description = "Gestion des favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/addQuincaFavorite")
    @Operation(summary = "Mettre une Quincaillerie en favoris")
    public ResponseEntity<?> addQuincaFavorite(@Valid @RequestBody AddFavoriteQuincaillerieDTO addFavoriteQuincaillerieDTO) {
        favoriteService.addFavoriteQuincaillerie(addFavoriteQuincaillerieDTO);
        return ResponseEntity.ok("Quincaillerie mise en favoris avec succes");
    }

    @DeleteMapping("/deleteQuincaFavorite")
    @Operation(summary = "Supprimer une Quincaillerie des favoris")
    public ResponseEntity<?> deleteQuincaFavorite(@Valid @RequestBody DeleteFavoriteQuincaillerieDTO deleteFavoriteQuincaillerieDTO) {
        favoriteService.deleteFavoriteQuincaillerie(deleteFavoriteQuincaillerieDTO);
        return ResponseEntity.ok("Quincaillerie supprimé des favoris avec succes");
    }

    @PostMapping("/addProductFavorite")
    @Operation(summary = "Mettre un produit en favoris")
    public ResponseEntity<?> addProductFavorite(@Valid @RequestBody AddFavoriteProductDTO addFavoriteProductDTO){
        favoriteService.addFavoriteProduct(addFavoriteProductDTO);
        return ResponseEntity.ok("Produit mis en favoris avec succes");
    }

    @DeleteMapping("/deleteProductFavorite")
    @Operation(summary = "Supprimer un produit des favoris")
    public ResponseEntity<?> deleteProductFavorite(@Valid @RequestBody DeleteFavoriteProductDTO deleteFavoriteProductDTO) {
        favoriteService.deleteFavoriteProduct(deleteFavoriteProductDTO);
        return ResponseEntity.ok("Produit supprimé des favoris avec succes");
    }
}

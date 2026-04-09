package com.ict300.P04.DTO.product.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  @AllArgsConstructor @NoArgsConstructor
public class getProductSuggestionDTO {
    private String id;
    private String nom;
    private String categorieId;
    private String categoryName;
    private String descriptionProduit;
    private String descriptionCategorie;
    private String brand;
    private String unite;
}

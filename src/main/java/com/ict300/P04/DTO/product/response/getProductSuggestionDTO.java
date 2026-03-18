package com.ict300.P04.DTO.product.response;

import lombok.Data;

@Data
public class getProductSuggestionDTO {
    private String id;
    private String nom;
    private String categorieId;
    private String categoryName;
    private String descriptionProduit;
    private String descriptionCategorie;
    private String brand;
    private String unite;

    public getProductSuggestionDTO(String id , String nom , String categorieId , String categoryName , String descriptionCategorie , String descriptionProduit , String brand , String unite){
        this.id = id;
        this.nom = nom;
        this.categorieId = categorieId;
        this.categoryName = categoryName;
        this.descriptionProduit = descriptionProduit;
        this.descriptionCategorie = descriptionCategorie;
        this.brand = brand;
        this.unite = unite;
    }

    public getProductSuggestionDTO(){}
}

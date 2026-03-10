
package com.ict300.P04.DTO.product.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
public class UpdateProductDTO {

    private String name;                    // Optionnel : nouveau nom
    private String brand;                   // Optionnel : marque
    private String descriptionProduit;      // Optionnel : description
    private BigDecimal purchasePrice;       // Optionnel : prix d'achat
    private BigDecimal sellingPrice;        // Optionnel : prix de vente
    private Integer quantite;               // Optionnel : stock / quantité
    private String unite;                   // Optionnel : unité (Unité, Sac, Kilo...)
    private String imageUrl;                // Optionnel : nouvelle URL image si changé

    public UpdateProductDTO (String name , String brand , String descriptionProduit , BigDecimal purchasePrice , BigDecimal sellingPrice , Integer quantite , String unite , String imageUrl) {
        this.name = name;
        this.brand = brand;
        this.descriptionProduit = descriptionProduit;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.quantite = quantite;
        this.unite = unite;
        this.imageUrl = imageUrl;
    }

    public UpdateProductDTO(){}
}

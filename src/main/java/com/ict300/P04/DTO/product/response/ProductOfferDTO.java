package com.ict300.P04.DTO.product.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductOfferDTO {
    // Identifiants
    private String idPrice;          // L'ID unique de cette offre (Prix + Quincaillerie + Produit)
    private String idProduct;        // Pratique à avoir
    private String idCategory;
    private String idQuincaillerie;

    // Infos Produit
    private String name;
    private String brand;
    private String unit;
    private String imageUrl;
    private String description;

    // Infos Quincaillerie
    private String quincaillerieName;
    private BigDecimal latitudeQuincaillerie;
    private BigDecimal longitudeQuincaillerie;

    // Tarification et Stock
    private int stock;
    private BigDecimal purchasePrice; // Prix d'achat (utile pour la vue Stock/Admin)
    private BigDecimal sellPrice;     // Prix de vente normal

    // Promotions (Toujours présents, mis à false/null/0 si pas de promo)
    private boolean inPromotion;
    private BigDecimal pricePromo;
    private Double taux;
}

package com.ict300.P04.DTO.category.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data  @AllArgsConstructor @NoArgsConstructor
public class ProductInCategoryDTO {
    private String idPrice;
    private String quincaillerieName;
    private String idQuincaillerie;
    private BigDecimal latitudeQuincaillerie;
    private BigDecimal longitudeQuincaillerie;
    private String name;
    private String brand;
    private String idCategory;
    private int stock;
    private String unit;
    private BigDecimal sellPrice;
    private String imageUrl;
    private String descriptionProduit;
    private BigDecimal purchasePrice;
    private BigDecimal pricepromo;
    private boolean inPromotion;
    private Double taux;
}

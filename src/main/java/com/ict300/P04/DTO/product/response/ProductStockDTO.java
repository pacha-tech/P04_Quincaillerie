package com.ict300.P04.DTO.product.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class ProductStockDTO {
    private String id;
    private String name;
    private String brand;
    private String category;
    private int stock;
    private String unit;
    private String sellPrice;
    private String imageUrl;
    private String description;
    private String purchasePrice;
    private String pricePromo;
    private boolean inPromotion;
    private String taux;

}

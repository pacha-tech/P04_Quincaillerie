package com.ict300.P04.DTO.recommadation.response;

import com.ict300.P04.Entite.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data  @AllArgsConstructor @NoArgsConstructor
public class RecommendedProductDTO {
    private String idPrice;
    private String name;
    private BigDecimal price;
    private String description;
    private int stock;
    private int score;
    private String unite;
    private double pricePromo;
    private boolean inPromo;
    private String imageUrl;
    private String taux;
}

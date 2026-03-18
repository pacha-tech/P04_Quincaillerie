package com.ict300.P04.DTO.recommadation.response;

import com.ict300.P04.Entite.Product;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecommendedProductDTO {
    private String idPrice;
    private String name;
    private BigDecimal price;
    private String description;
    private int stock;
    private int score;
    private String unite;

    public RecommendedProductDTO(String idPrice , String name , BigDecimal price , String description , int stock , int score , String unite) {
        this.idPrice = idPrice;
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
        this.score = score;
        this.unite = unite;
    }
}

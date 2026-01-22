package com.ict300.P04.DTO.recommadation.response;

import com.ict300.P04.Entite.Product;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecommendedProductDTO {
    private String name;
    private BigDecimal price;
    private String description;
    private int score;

    public RecommendedProductDTO(String name , BigDecimal price , String description , int score) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.score = score;
    }
}

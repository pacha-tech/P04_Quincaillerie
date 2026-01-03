package com.ict300.P04.DTO.price.response;

import com.ict300.P04.Entite.Quincaillerie;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceSearchProductDTO {
    private String quincaillerieName;
    private BigDecimal price;
    private String stock;
    private String promotionRating;

    public PriceSearchProductDTO(String quincaillerieNAme , BigDecimal price , String stock , String promotionRating){
        this.quincaillerieName = quincaillerieNAme;
        this.price = price;
        this.stock = stock;
        this.promotionRating = promotionRating;
    }
}

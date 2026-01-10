package com.ict300.P04.DTO.price.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceSearchProductDTO {
    private String quincaillerieName;
    private BigDecimal price;
    private String stock;
    private String promotionRating;
    private BigDecimal latitudeQuincaillerie;
    private BigDecimal longitudeQuincaillerie;

    public PriceSearchProductDTO(String quincaillerieNAme , BigDecimal price , String stock , String promotionRating , BigDecimal latitudeQuincaillerie , BigDecimal longitudeQuincaillerie){
        this.quincaillerieName = quincaillerieNAme;
        this.price = price;
        this.stock = stock;
        this.promotionRating = promotionRating;
        this.latitudeQuincaillerie = latitudeQuincaillerie;
        this.longitudeQuincaillerie = longitudeQuincaillerie;
    }
}

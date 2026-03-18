package com.ict300.P04.DTO.price.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceSearchProductDTO {
    private String quincaillerieName;
    private String idQuincaillerie;
    private BigDecimal price;
    private int stock;
    private String idPrice;
    private BigDecimal latitudeQuincaillerie;
    private BigDecimal longitudeQuincaillerie;

    public PriceSearchProductDTO(String quincaillerieName , String idQuincaillerie , BigDecimal price , int stock , String idPrice , BigDecimal latitudeQuincaillerie , BigDecimal longitudeQuincaillerie){
        this.quincaillerieName = quincaillerieName;
        this.idQuincaillerie = idQuincaillerie;
        this.price = price;
        this.stock = stock;
        this.idPrice = idPrice;
        this.latitudeQuincaillerie = latitudeQuincaillerie;
        this.longitudeQuincaillerie = longitudeQuincaillerie;
    }
}

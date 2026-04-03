package com.ict300.P04.DTO.price.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data  @AllArgsConstructor @NoArgsConstructor
public class PriceSearchProductDTO {
    private String quincaillerieName;
    private String idQuincaillerie;
    private BigDecimal price;
    private int stock;
    private String idPrice;
    private BigDecimal latitudeQuincaillerie;
    private BigDecimal longitudeQuincaillerie;
    private String pricePromo;
    private boolean inPromotion;
    private String taux;

}

package com.ict300.P04.DTO.promotion.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @AllArgsConstructor @NoArgsConstructor
public class ProduitPromotionDTO {
    private String id;
    private String nom;
    private String category;
    private BigDecimal price;
}

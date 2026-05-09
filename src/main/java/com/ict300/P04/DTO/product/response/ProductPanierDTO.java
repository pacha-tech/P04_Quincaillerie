package com.ict300.P04.DTO.product.response;

import com.google.type.Decimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data @AllArgsConstructor @NoArgsConstructor
public class ProductPanierDTO {
    private String idPrice;
    private String idQuincaillerie;
    private String productName;
    private String storeName;
    private boolean inPromotion;
    private Double pricePromo;
    private BigDecimal price;
    private int quantity;
    private String imageUrl;
    private int stock;
}

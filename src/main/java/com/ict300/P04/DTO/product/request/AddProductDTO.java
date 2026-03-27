package com.ict300.P04.DTO.product.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @AllArgsConstructor @NoArgsConstructor
public class AddProductDTO {
    private String name;
    private String categoryId;
    private String brand;
    private String description;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private int stock;
    private String unite;

}

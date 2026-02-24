package com.ict300.P04.DTO.product.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddProductDTO {
    private String imageUrl;
    private String name;
    private String categoryId;
    private String brand;
    private String description;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private int stock;
    private String idQuincaillerie;
    private String unite;

    public AddProductDTO(String imageUrl , String name , String categoryId , String brand , String description , BigDecimal purchasePrice , BigDecimal sellingPrice , int stock , String idQuincaillerie , String unite) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.categoryId = categoryId;
        this.brand = brand;
        this.description = description;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.stock = stock;
        this.idQuincaillerie = idQuincaillerie;
        this.unite = unite;
    }

    public AddProductDTO(){}
}

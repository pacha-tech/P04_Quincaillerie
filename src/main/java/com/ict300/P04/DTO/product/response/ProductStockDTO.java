package com.ict300.P04.DTO.product.response;

import lombok.Data;

@Data
public class ProductStockDTO {
    private String id;
    private String name;
    private String brand;
    private String category;
    private int stock;
    private String unit;
    private String sellPrice;
    private String imageUrl;
    private String description;
    private String purchasePrice;

    public ProductStockDTO(String id , String name , String brand ,String category , int stock , String unit , String sellPrice , String imageUrl , String description , String purchasePrice){
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.stock = stock;
        this.unit = unit;
        this.sellPrice = sellPrice;
        this.imageUrl = imageUrl;
        this.description = description;
        this.purchasePrice = purchasePrice;
    }

    public ProductStockDTO(){}
}

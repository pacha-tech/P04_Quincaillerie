package com.ict300.P04.DTO.commande.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class CommandeDetailDTO {
    private String nameProduct;
    private double price;
    private int quantity;
}

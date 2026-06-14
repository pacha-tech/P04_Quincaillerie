package com.ict300.P04.DTO.quincaillerie.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class TopProduit {
    private Integer rang;
    private String idPrice;
    private String imageUrl;
    private String nom;
    private Integer quantiteVendue;
    private String tendance;
}

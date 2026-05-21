package com.ict300.P04.DTO.statistique.detailProduct.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class GraphiqueMouvementDTO {
    private String labelPeriode;
    private int totalEntrees;
    private int totalSorties;
}

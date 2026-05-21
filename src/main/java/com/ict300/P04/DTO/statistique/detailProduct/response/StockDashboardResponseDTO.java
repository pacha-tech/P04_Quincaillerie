package com.ict300.P04.DTO.statistique.detailProduct.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class StockDashboardResponseDTO {
    private String idPrice;

    private int stockActuel;

    private DernierMouvementDTO dernierMouvement;

    private IndicateursPerformanceDTO indicateurs;

    private List<GraphiqueMouvementDTO> donneesGraphique;
}

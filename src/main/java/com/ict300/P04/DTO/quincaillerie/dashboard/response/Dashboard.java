package com.ict300.P04.DTO.quincaillerie.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class Dashboard {
    private StatistiquesCles statistiquesCles;
    private BigDecimal fondsEnAttente;
    private List<TopProduit> topProduits;
}

package com.ict300.P04.DTO.quincaillerie.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @AllArgsConstructor @NoArgsConstructor
public class ChiffreAffaires {
    private BigDecimal montant;
    private Double evolutionPourcentage;
    private String tendance;
}

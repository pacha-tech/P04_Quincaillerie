package com.ict300.P04.DTO.vente.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @AllArgsConstructor @NoArgsConstructor
public class VentesStatsDTO {
    private String date;
    private BigDecimal chiffreAffaires;
    private long commandePayees;
}

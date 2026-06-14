package com.ict300.P04.DTO.quincaillerie.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class StatistiquesCles {
    private ChiffreAffaires chiffreAffaires;
    private Reputation reputation;
    private Integer produitsActifs;
}

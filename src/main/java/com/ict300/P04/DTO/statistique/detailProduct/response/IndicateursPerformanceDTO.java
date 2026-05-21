package com.ict300.P04.DTO.statistique.detailProduct.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor
public class IndicateursPerformanceDTO {
    private int stockActuel;
    private String nameProduct;
    private int ventesSurPeriode;
    private double moyenneVentesParSemaine;
    private LocalDateTime dateDernierReassort;
    private int pertesSurPeriode;
}

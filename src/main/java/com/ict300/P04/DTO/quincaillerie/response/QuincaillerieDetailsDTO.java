package com.ict300.P04.DTO.quincaillerie.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class QuincaillerieDetailsDTO {
    private String name;
    private String ville;
    private String quartier;
    private String telephone;
    private BigDecimal averageRating;
    private String status;
}

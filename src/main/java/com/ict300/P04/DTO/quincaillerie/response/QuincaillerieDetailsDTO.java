package com.ict300.P04.DTO.quincaillerie.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data  @AllArgsConstructor @NoArgsConstructor
public class QuincaillerieDetailsDTO {
    private String name;
    private String region;
    private String ville;
    private String quartier;
    private String precision;
    private String telephone;
    private BigDecimal averageRating;
    private String photoUrl;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String status;
    private String description;
}

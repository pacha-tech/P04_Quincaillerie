package com.ict300.P04.DTO.promotion.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class AddPromotionDTO {
    private String nom;

    @NotNull
    private BigDecimal tauxRemise;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateDebut;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateFin;
    List<String> idsPrices;
}

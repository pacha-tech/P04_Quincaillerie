package com.ict300.P04.DTO.quincaillerie.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class ComptePaiementDTO {
    private String nomCompte;
    private String numeroTelephone;
    private String operateur;
}

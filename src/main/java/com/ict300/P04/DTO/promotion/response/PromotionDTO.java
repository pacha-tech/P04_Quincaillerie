package com.ict300.P04.DTO.promotion.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class PromotionDTO {
    private String name;
    private String taux;
    private String dateDebut;
    private String dateFin;
    private boolean estActif;
    private int nbreProduits;
}

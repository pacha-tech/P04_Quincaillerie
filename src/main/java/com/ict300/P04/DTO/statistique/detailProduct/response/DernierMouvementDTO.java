package com.ict300.P04.DTO.statistique.detailProduct.response;

import com.ict300.P04.Utilitaires.MouvementStock;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor
public class DernierMouvementDTO {
    private LocalDateTime date;
    private MouvementStock type;
    private int quantite;
    private String commentaire;
}

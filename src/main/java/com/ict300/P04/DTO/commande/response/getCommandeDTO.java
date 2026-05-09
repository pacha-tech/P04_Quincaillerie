package com.ict300.P04.DTO.commande.response;

import com.ict300.P04.Utilitaires.StatutCommande;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor
public class getCommandeDTO {
    private String idCommande;
    private LocalDateTime dateCommande;
    private StatutCommande statut;
    private Double montantTotal;
    private int nombreArticles;
    private String nomQuincaillerie;
    private String factureUrl;
    private String clientName;
}

package com.ict300.P04.DTO.commande.response;

import com.ict300.P04.Utilitaires.StatutCommande;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandeResponseDTO {
    private String idCommande;
    private String message;
}

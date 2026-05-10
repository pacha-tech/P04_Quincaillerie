package com.ict300.P04.DTO.commande.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class CommandeStatsDTO {
    private String date;
    private Long payee;
    private Long livree;
    private Long annulee;
    private Long a_validee;
    private Long a_payee;
}

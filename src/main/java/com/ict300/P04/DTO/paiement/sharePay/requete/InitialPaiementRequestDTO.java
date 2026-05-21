package com.ict300.P04.DTO.paiement.sharePay.requete;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class InitialPaiementRequestDTO {
    private int amount;
    private String currency;
    private String merchantReference;
    private String description;
    private String successUrl;
    private String cancelUrl;
}

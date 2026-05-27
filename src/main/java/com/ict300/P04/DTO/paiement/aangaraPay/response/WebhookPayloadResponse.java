package com.ict300.P04.DTO.paiement.aangaraPay.response;

import com.ict300.P04.Utilitaires.StatutPaiement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class WebhookPayloadResponse {
    private String transaction_id;
    private StatutPaiement status;
    private int amount;
    private String operator;
    private String paytoken;
    private String txnid;
    private String phone_number;
    private String description;
    private String timestamp;
}

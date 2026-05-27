package com.ict300.P04.DTO.paiement.aangaraPay.response;

import com.ict300.P04.Utilitaires.StatutPaiement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class PaymentStatusResponse {
    private boolean success;
    private StatutPaiement status;
    private String operator;
    private String transactionId;
    private String payToken;
    private double amount;
    private String currency;
    private String message;
    private String operatorCode;
    private String timestamp;
    private String phoneNumber;
    private PaymentDetails details;
}

package com.ict300.P04.DTO.paiement.aangaraPay.response;

import com.ict300.P04.Utilitaires.StatutPaiement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class WithdrawalStatusResponse {
    private boolean success;
    private StatutPaiement status;
    private String operator;
    private String transactionId;
    private double amount;
    private String currency;
    private String message;
    private String operatorCode;
    private String timestamp;
    private WithdrawalDetails details;
}

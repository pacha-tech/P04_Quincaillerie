package com.ict300.P04.DTO.paiement.aangaraPay.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class WithdrawalData {
    private String status;
    private String referenceId;
    private String transactionId;
    private String amount;
    private String phoneNumber;
    private String paymentMethod;
    private String message;
}

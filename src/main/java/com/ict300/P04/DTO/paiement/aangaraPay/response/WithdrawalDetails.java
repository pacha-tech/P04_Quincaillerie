package com.ict300.P04.DTO.paiement.aangaraPay.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class WithdrawalDetails {
    private String financialTransactionId;
    private String reason;
}

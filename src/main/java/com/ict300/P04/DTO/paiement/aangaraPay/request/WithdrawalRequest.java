package com.ict300.P04.DTO.paiement.aangaraPay.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class WithdrawalRequest {
    private String appKey;
    private String phoneNumber;
    private String amount;
    private String paymentMethod;
    private String username;
}

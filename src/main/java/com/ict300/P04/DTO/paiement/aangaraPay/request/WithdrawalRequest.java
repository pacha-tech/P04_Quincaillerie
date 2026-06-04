package com.ict300.P04.DTO.paiement.aangaraPay.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class WithdrawalRequest {
    private String app_key;
    private String phone_number;
    private String amount;
    private String payment_method;
    private String username;
}

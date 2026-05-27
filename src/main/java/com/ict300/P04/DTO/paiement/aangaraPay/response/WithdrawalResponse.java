package com.ict300.P04.DTO.paiement.aangaraPay.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class WithdrawalResponse {
    private int statusCode;
    private String message;
    private WithdrawalData data;
}

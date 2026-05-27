package com.ict300.P04.DTO.paiement.aangaraPay.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class PaymentData {
    private String payment_url;
    private String transaction_id;
    private Long paymentHistory_id;
    private String pay_token;
}

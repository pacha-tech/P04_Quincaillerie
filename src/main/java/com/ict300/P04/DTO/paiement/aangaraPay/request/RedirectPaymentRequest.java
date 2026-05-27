package com.ict300.P04.DTO.paiement.aangaraPay.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class RedirectPaymentRequest {
    private double amount;
    private String description;
    private String app_key;
    private String transaction_id;
    private String return_url;
    private String notify_url;
    private String operator;
    private String devise_id = "XAF";
}

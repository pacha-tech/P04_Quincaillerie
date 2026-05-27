package com.ict300.P04.DTO.paiement.aangaraPay.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class NoRedirectPaymentRequest extends RedirectPaymentRequest {
    private String phoneNumber;
}

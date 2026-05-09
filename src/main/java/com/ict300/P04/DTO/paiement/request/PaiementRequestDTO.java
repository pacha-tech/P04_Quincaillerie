package com.ict300.P04.DTO.paiement.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class PaiementRequestDTO {
    private String phoneNumber;
    private String paymentMethod;
    private String CommandeId;
}

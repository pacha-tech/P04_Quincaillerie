package com.ict300.P04.DTO.paiement.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class PaiementResponseDTO {
    private boolean success;
    private String message;
    private String transactionId;
    private String factureUrl;
    private LocalDateTime paiementDate;
}

package com.ict300.P04.DTO.paiement.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  @AllArgsConstructor @NoArgsConstructor
public class RetraitResponseDTO {
    private boolean success;
    private String message;
}

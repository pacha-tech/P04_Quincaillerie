package com.ict300.P04.DTO.paiement.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor
public class CodeOtpResponseDTO {
    private String code;
    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;
}

package com.ict300.P04.DTO.user.customer.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String message;
}

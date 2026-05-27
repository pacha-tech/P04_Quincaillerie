package com.ict300.P04.DTO.paiement.aangaraPay.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class GetUserInfoRequest {
    private String msisdn;
    private String apiKey;
    private String country;
    private String operator;
}

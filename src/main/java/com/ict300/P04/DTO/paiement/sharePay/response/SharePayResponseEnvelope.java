package com.ict300.P04.DTO.paiement.sharePay.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class SharePayResponseEnvelope<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;
    private String timestamp;
}

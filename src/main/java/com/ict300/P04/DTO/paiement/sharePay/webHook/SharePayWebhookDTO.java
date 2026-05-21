package com.ict300.P04.DTO.paiement.sharePay.webHook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SharePayWebhookDTO {
    private String event;
    private String timestamp;
    private DataWebHook data;
}
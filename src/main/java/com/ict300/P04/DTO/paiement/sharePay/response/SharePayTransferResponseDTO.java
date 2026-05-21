package com.ict300.P04.DTO.paiement.sharePay.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data @AllArgsConstructor @NoArgsConstructor
public class SharePayTransferResponseDTO {
    private String reference;
    private String status;
    private Integer amount;
    private String currency;
    private String paymentMethod;
    private String beneficiaryAccount;
    private String beneficiaryName;
}

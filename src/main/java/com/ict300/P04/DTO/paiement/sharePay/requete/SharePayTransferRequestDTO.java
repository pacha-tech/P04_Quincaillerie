package com.ict300.P04.DTO.paiement.sharePay.requete;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SharePayTransferRequestDTO {
    private Integer amount;
    private String currency;
    private String paymentMethod;
    private String beneficiaryAccount;
    private String beneficiaryName;
    private String merchantReference;
    private String description;
    private String beneficiaryEmail;
}

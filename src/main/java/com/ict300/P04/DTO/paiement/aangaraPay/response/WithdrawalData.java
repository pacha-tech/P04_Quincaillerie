package com.ict300.P04.DTO.paiement.aangaraPay.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class WithdrawalData {
    /*
    private String status;
    private String reference_id;
    private String transaction_id;
    private String amount;
    private String phone_number;
    private String payment_method;
    private String message;
     */
    private  int code;
    private int statut_code;
    private String message;
    private String messageId;
    private int withdrawal_id;
}

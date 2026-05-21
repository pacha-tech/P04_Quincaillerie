package com.ict300.P04.DTO.paiement.redix;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendeurContextDTO implements Serializable {
    private String ipVendeur;
    private String userAgentVendeur;
}

package com.ict300.P04.DTO.paiement.aangaraPay.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data  @NoArgsConstructor @AllArgsConstructor
public class BalanceData {
    private Long serviceId;
    private String serviceName;
    private double totalBalance;
    private String currency;
    private List<OperatorBalance> operators;
    private String lastUpdated;
}

package com.ict300.P04.Exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  @AllArgsConstructor @NoArgsConstructor
public class ApiResponse {
    private boolean succes;
    private String message;
}

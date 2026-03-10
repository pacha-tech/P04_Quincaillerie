package com.ict300.P04.Exception;

import lombok.Data;

@Data
public class ApiResponse {
    private boolean succes;
    private String message;

    public ApiResponse(boolean succes , String message) {
        this.succes = succes;
        this.message = message;
    }
}

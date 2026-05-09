package com.ict300.P04.Exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

@Data
public class ApiError {
    private boolean success;
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;

    public ApiError(HttpStatus status, String message) {
        this.success = false;
        this.timestamp = LocalDateTime.now();
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
    }
}
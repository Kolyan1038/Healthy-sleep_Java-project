package org.example.exception;

import java.util.List;
import lombok.Data;

@Data
public class ErrorResponse {
    private int status;
    private String message;
    private List<String> details;
    
    public ErrorResponse(int status, String message, List<String> details) {
        this.status = status;
        this.message = message;
        this.details = details;
    }
}
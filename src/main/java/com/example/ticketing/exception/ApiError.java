package com.example.ticketing.exception;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class ApiError {
    private int status;
    private String error;
    private String message;
    private Map<String, String> validationErrors;
    @Builder.Default
    private Instant timestamp = Instant.now();
}

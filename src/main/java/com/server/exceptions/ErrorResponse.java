package com.server.exceptions;

import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record ErrorResponse(
        @JsonProperty("error_message")
        String message,

        @JsonProperty("error_code")
        int statusCode,

        @JsonProperty("timestamp")
        LocalDateTime timestamp
) {
    public ErrorResponse(String message, HttpStatus status) {
        this(message, status.value(), LocalDateTime.now());
    }

    public ErrorResponse(String message, int statusCode) {
        this(message, statusCode, LocalDateTime.now());
    }
}

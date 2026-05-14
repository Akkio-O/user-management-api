package com.server.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse(
        int statusCode,
        Object body,
        Integer total,
        String message,
        LocalDateTime timestamp
) {
    public ApiResponse(int statusCode, Object body) {
        this(statusCode, body, null, null, LocalDateTime.now());
    }

    public ApiResponse(int statusCode, Object body, String message) {
        this(statusCode, body, null, message, LocalDateTime.now());
    }

    public ApiResponse(int statusCode, Object body, Integer total) {
        this(statusCode, body, total, null, LocalDateTime.now());
    }

    // Конструктор для ошибок с сообщением
    public ApiResponse(int statusCode, String message) {
        this(statusCode, null, null, message, LocalDateTime.now());
    }
}

package com.server.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse(
        int statusCode,
        Object body,
        String message,
        LocalDateTime timestamp,
        PageMeta pageMeta
) {
    public ApiResponse(int statusCode, Object body) {
        this(statusCode, body, null, LocalDateTime.now(), null);
    }

    public ApiResponse(int statusCode, Object body, String message) {
        this(statusCode, body, message, LocalDateTime.now(), null);
    }

    public ApiResponse(int statusCode, Object body, PageMeta pageMeta) {
        this(statusCode, body, null, LocalDateTime.now(), pageMeta);
    }
}
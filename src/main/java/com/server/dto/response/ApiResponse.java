package com.server.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse(
        Integer statusCode,
        Object body,
        String message,
        LocalDateTime timestamp,
        PageMeta pageMeta,
        String token
) {
    public ApiResponse(Integer statusCode,Object body,String message,PageMeta pageMeta) {
        this(statusCode,body,message,LocalDateTime.now(),pageMeta, null);
    }
    public ApiResponse(Integer statusCode,Object body,String message, String token) {
        this(statusCode,body,message,LocalDateTime.now(),null, token);
    }
    public ApiResponse(Integer statusCode,Object body) {
        this(statusCode,body,null,LocalDateTime.now(),null, null);
    }
}
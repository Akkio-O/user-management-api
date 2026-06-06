package com.server.dto.response;

public record PageMeta(
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {}
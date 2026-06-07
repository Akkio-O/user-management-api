package com.server.dto.response;

import java.util.List;

public record PageResponse(
        List<?> content,
        PageMeta meta
) {
}
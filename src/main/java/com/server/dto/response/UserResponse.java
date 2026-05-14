package com.server.dto.response;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String login,
        String firstName,
        String lastName,
        Integer age,
        AddressResponse address
) {}
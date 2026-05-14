package com.server.dto.response;

public record AddressResponse(
        String city,
        String street,
        String building
) {}

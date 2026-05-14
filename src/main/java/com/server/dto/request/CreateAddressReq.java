package com.server.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateAddressReq(
        @NotBlank
        String city,
        @NotBlank
        String street,
        @NotBlank
        String building
){}

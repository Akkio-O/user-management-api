package com.server.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserReq(
        @NotBlank
        String login,
        @NotBlank
        @Size(min = 8, max = 20)
        String password,
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        Integer age,
        @Valid
        @NotNull
        CreateAddressReq address
) {}
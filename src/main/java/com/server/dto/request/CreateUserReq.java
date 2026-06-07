package com.server.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserReq(
        @NotBlank(message = "Логин не должен быть пустым")
        String login,
        @NotBlank(message = "Пароль не должен быть пустым")
        @Size(min = 8, max = 20)
        String password,
        @NotBlank(message = "Имя не должно быть пустым")
        String firstName,
        @NotBlank(message = "Фамилия не должно быть пустым")
        String lastName,
        Integer age,
        @Valid
        @NotNull(message = "Адрес должен быть указан")
        CreateAddressReq address
) {}
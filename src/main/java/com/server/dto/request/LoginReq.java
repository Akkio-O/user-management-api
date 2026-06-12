package com.server.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginReq(
        @NotBlank(message = "Требуется войти в систему")
        String login,

        @NotBlank(message = "Требуется ввести пароль")
        String password
)
{}

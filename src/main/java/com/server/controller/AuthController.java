package com.server.controller;

import com.server.dto.request.CreateUserReq;
import com.server.dto.request.LoginReq;
import com.server.dto.response.ApiResponse;
import com.server.dto.response.LoginResponse;
import com.server.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody CreateUserReq user) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(user));
    }

    @PostMapping(
            value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginReq user) {
//        log.info("Получен запрос на вход в систему для пользователя: {}", user.login());
        LoginResponse response = authService.login(user);
        return ResponseEntity.ok(response);
    }
}

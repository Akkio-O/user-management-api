package com.server.service;

import com.server.controller.AuthController;
import com.server.dto.request.CreateUserReq;
import com.server.dto.request.LoginReq;
import com.server.dto.response.ApiResponse;
import com.server.dto.response.LoginResponse;
import com.server.dto.response.UserResponse;
import com.server.entity.User;
import com.server.exceptions.ResourceConflictException;
import com.server.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.server.mapper.UserMapper.mapToUserEntity;
import static com.server.mapper.UserMapper.mapToUserResponse;

@Slf4j
@Service
public class AuthService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public ApiResponse register(CreateUserReq userReq) {
        if (userRepository.existsByLogin(userReq.login())) {
            throw new ResourceConflictException("Логин " + userReq.login() + " уже существует");
        }

        User user = mapToUserEntity(userReq);

        user.setPasswordHash(passwordEncoder.encode(userReq.password()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setRole("USER");

        User saved = userRepository.save(user);
        UserResponse savedUser = mapToUserResponse(saved);
        String token = jwtService.generateToken(saved);
        return new ApiResponse(201, savedUser, "Новый пользователь " + savedUser.login() + " создан", token);
    }

    public LoginResponse login(LoginReq request) {
        log.info("Получаем данные УЗ: {}", request);
        User user = userRepository.findByLogin(request.login())
                .orElseThrow(() ->
                        new RuntimeException("Неверный логин или пароль")
                );
        log.info("Получаем данные user: {}", user);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Неверный пароль для пользователя: {}", user.getLogin());
            throw new BadCredentialsException("Неверные учётные данные");
        }
        String token = jwtService.generateToken(user);
        log.info("Получаем токен: {}", token);
        return new LoginResponse(token);
    }
}

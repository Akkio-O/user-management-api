package com.server.service;

import com.server.dto.request.CreateUserReq;
import com.server.dto.response.ApiResponse;
import com.server.dto.response.UserResponse;
import com.server.entity.User;
import com.server.exceptions.ResourceConflictException;
import com.server.exceptions.ResourceNotFoundException;
import com.server.mapper.UserMapper;
import com.server.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.server.mapper.UserMapper.mapToUserEntity;
import static com.server.mapper.UserMapper.mapToUserResponse;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public ApiResponse findAll() {
        List<UserResponse> users = userRepository.findAll().stream()
                .map(UserMapper::mapToUserResponse)
                .toList();
        return new ApiResponse(200, users, users.size());
    }

    @Transactional(readOnly = true)
    public ApiResponse findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с ID " + id + " не найден"));

        return new ApiResponse(200, mapToUserResponse(user));
    }

    @Transactional
    public void deleteById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с ID " + id + " не найден"));
        userRepository.delete(user);
    }

    @Transactional
    public ApiResponse update(UUID id, CreateUserReq userReq) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с ID " + id + " не найден"));
        if (!user.getLogin().equals(userReq.login()) && userRepository.existsByLogin(userReq.login())) {
            throw new ResourceConflictException("Логин " + userReq.login() + " уже существует");
        }
        mapToUserEntity(userReq, user);

        user.setPasswordHash(passwordEncoder.encode(userReq.password()));
        user.setUpdatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);
        UserResponse savedUser = mapToUserResponse(saved);
        return new ApiResponse(200, savedUser, "Изменены данные пользователя");
    }

    @Transactional
    public ApiResponse create(CreateUserReq userReq) {
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
        return new ApiResponse(201, savedUser, "Новый пользователь " + savedUser.login() + " создан");
    }
}

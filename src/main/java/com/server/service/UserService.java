package com.server.service;

import com.server.dto.request.CreateUserReq;
import com.server.dto.response.ApiResponse;
import com.server.dto.response.PageMeta;
import com.server.dto.response.UserResponse;
import com.server.entity.User;
import com.server.exceptions.ResourceConflictException;
import com.server.exceptions.ResourceNotFoundException;
import com.server.exceptions.Validate;
import com.server.mapper.UserMapper;
import com.server.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.ServiceUnavailableException;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.server.mapper.UserMapper.mapToUserEntity;
import static com.server.mapper.UserMapper.mapToUserResponse;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable) throws ServiceUnavailableException {
        Validate.validatePageable(pageable);
        log.info("Выборка пользователей: {}", pageable);
        try {
            Page<User> page = userRepository.findAll(pageable);
            Page<UserResponse> mapped = page.map(UserMapper::mapToUserResponseSafe);
            PageMeta meta = new PageMeta(
                    page.getNumber(),
                    page.getSize(),
                    page.getTotalElements(),
                    page.getTotalPages()
            );
            return new ApiResponse(200, mapped.getContent(), meta);
        } catch (DataAccessException e) {
            log.error("Ошибка базы данных", e);
            throw new ServiceUnavailableException("БД недоступна");
        }
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

package com.server.service;

import com.server.dto.request.CreateAddressReq;
import com.server.dto.request.CreateUserReq;
import com.server.dto.response.ApiResponse;
import com.server.dto.response.UserResponse;
import com.server.entity.User;
import com.server.exceptions.ResourceConflictException;
import com.server.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {
    private static final String RAW_PASSWORD = "password123";

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void createShouldStorePasswordHashInsteadOfRawPassword() {
        CreateUserReq request = userRequest("evgeniy", RAW_PASSWORD);

        userService.create(request);

        User saved = userRepository.findAll().getFirst();
        assertThat(saved.getPasswordHash()).isNotEqualTo(RAW_PASSWORD);
        assertThat(passwordEncoder.matches(RAW_PASSWORD, saved.getPasswordHash())).isTrue();
    }

    @Test
    void createShouldThrowConflictWhenLoginAlreadyExists() {
        CreateUserReq request = userRequest("evgeniy", RAW_PASSWORD);
        userService.create(request);

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("уже существует");
    }

    @Test
    void updateShouldAllowSameLoginForSameUser() {
        ApiResponse response = userService.create(userRequest("evgeniy", RAW_PASSWORD));
        UserResponse created = (UserResponse) response.body();

        ApiResponse updatedResponse = userService.update(
                created.id(),
                userRequest("evgeniy", "newpass123")
        );

        UserResponse updated = (UserResponse) updatedResponse.body();

        assertThat(updated.login()).isEqualTo("evgeniy");
    }

    private CreateUserReq userRequest(String login, String password) {
        return new CreateUserReq(
                login,
                password,
                "Евгений",
                "Нестеренко",
                26,
                new CreateAddressReq("Воронеж", "Ленина", "1")
        );
    }
}

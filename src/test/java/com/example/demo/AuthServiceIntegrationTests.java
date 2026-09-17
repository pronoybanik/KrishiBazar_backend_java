package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;

@SpringBootTest
class AuthServiceIntegrationTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void registerStoresHashedPasswordAndLoginSucceeds() {
        AuthResponse registered = authService.register(
                new RegisterRequest("Pronoy", "PRONOY@example.com", "12345678"));

        assertThat(registered.email()).isEqualTo("pronoy@example.com");
        assertThat(registered.role()).isEqualTo("USER");
        assertThat(registered.active()).isTrue();

        User savedUser = userRepository.findByEmail("pronoy@example.com").orElseThrow();
        assertThat(savedUser.getPassword()).isNotEqualTo("12345678");
        assertThat(passwordEncoder.matches("12345678", savedUser.getPassword())).isTrue();

        AuthResponse loggedIn = authService.login(
                new LoginRequest("pronoy@example.com", "12345678"));
        assertThat(loggedIn.message()).isEqualTo("Login successful");
    }

    @Test
    void duplicateEmailAndWrongPasswordAreRejected() {
        authService.register(new RegisterRequest("Pronoy", "pronoy@example.com", "12345678"));

        assertThatThrownBy(() -> authService.register(
                new RegisterRequest("Another User", "pronoy@example.com", "12345678")))
                .hasMessage("Email already registered");

        assertThatThrownBy(() -> authService.login(
                new LoginRequest("pronoy@example.com", "wrongpassword")))
                .hasMessage("Invalid email or password");
    }

    @Test
    void getAllUsersReturnsSafeUserData() {
        authService.register(new RegisterRequest("Pronoy", "pronoy@example.com", "12345678"));
        authService.register(new RegisterRequest("Mita", "mita@example.com", "87654321"));

        var users = authService.getAllUsers();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(UserResponse::email)
                .containsExactlyInAnyOrder("pronoy@example.com", "mita@example.com");
        assertThat(users).allSatisfy(user -> {
            assertThat(user.id()).isNotNull();
            assertThat(user.createdAt()).isNotNull();
            assertThat(user.updatedAt()).isNotNull();
        });
    }

    @Test
    void createAdminDoesNotRequireAnExistingAdmin() {
        AuthResponse created = authService.createAdmin(
                new RegisterRequest("New Admin", "NEW-ADMIN@example.com", "87654321"));

        assertThat(created.role()).isEqualTo("ADMIN");
        User savedAdmin = userRepository.findByEmail("new-admin@example.com").orElseThrow();
        assertThat(savedAdmin.getActive()).isTrue();
        assertThat(passwordEncoder.matches("87654321", savedAdmin.getPassword())).isTrue();
    }
}

package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.AuthData;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthData>> register(@Valid @RequestBody RegisterRequest request) {
        int statusCode = HttpStatus.CREATED.value();
        return ResponseEntity.status(statusCode)
                .body(new ApiResponse<>(true, statusCode, "Registration successful", toAuthData(authService.register(request))));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthData>> login(@Valid @RequestBody LoginRequest request) {
        int statusCode = HttpStatus.OK.value();
        return ResponseEntity.ok(new ApiResponse<>(true, statusCode, "Login successful", toAuthData(authService.login(request))));
    }

    private AuthData toAuthData(AuthResponse response) {
        return new AuthData(response.id(), response.name(), response.email(), response.role(), response.token());
    }
}

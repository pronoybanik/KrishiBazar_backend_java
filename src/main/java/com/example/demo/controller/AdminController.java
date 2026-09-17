package com.example.demo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.AuthData;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.FarmerApplicationResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.service.AuthService;
import com.example.demo.service.FarmerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final FarmerService farmerService;
    private final AuthService authService;

    public AdminController(FarmerService farmerService, AuthService authService) {
        this.farmerService = farmerService;
        this.authService = authService;
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<AuthData>> createAdmin(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.createAdmin(request);
        int statusCode = HttpStatus.CREATED.value();
        AuthData data = new AuthData(response.id(), response.name(), response.email(), response.role(), response.token());
        return ResponseEntity.status(statusCode)
                .body(new ApiResponse<>(true, statusCode, response.message(), data));
    }

    @GetMapping("/farmer-applications")
    public ResponseEntity<ApiResponse<List<FarmerApplicationResponse>>> getApplications(
            Authentication authentication) {
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(),
                "Farmer applications fetched successfully", farmerService.getApplications(userId(authentication))));
    }

    @PostMapping("/farmer-applications/{applicationId}/approve")
    public ResponseEntity<ApiResponse<FarmerApplicationResponse>> approve(
            Authentication authentication,
            @PathVariable UUID applicationId) {
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(),
                "Farmer application approved successfully",
                farmerService.approveApplication(userId(authentication), applicationId)));
    }

    @PostMapping("/farmer-applications/{applicationId}/reject")
    public ResponseEntity<ApiResponse<FarmerApplicationResponse>> reject(
            Authentication authentication,
            @PathVariable UUID applicationId) {
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(),
                "Farmer application rejected successfully",
                farmerService.rejectApplication(userId(authentication), applicationId)));
    }

    private UUID userId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }
}
package com.example.demo.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.FarmerApplicationRequest;
import com.example.demo.dto.FarmerApplicationResponse;
import com.example.demo.dto.FarmerProfileResponse;
import com.example.demo.service.FarmerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/farmers")
public class FarmerController {

    private final FarmerService farmerService;

    public FarmerController(FarmerService farmerService) {
        this.farmerService = farmerService;
    }

    @PostMapping("/application")
    public ResponseEntity<ApiResponse<FarmerApplicationResponse>> apply(
            Authentication authentication,
            @Valid @RequestBody FarmerApplicationRequest request) {
        int statusCode = HttpStatus.CREATED.value();
        return ResponseEntity.status(statusCode).body(new ApiResponse<>(true, statusCode,
                "Farmer application submitted successfully", farmerService.apply(userId(authentication), request)));
    }

    @GetMapping("/application")
    public ResponseEntity<ApiResponse<FarmerApplicationResponse>> getApplication(
            Authentication authentication) {
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(),
                "Farmer application fetched successfully", farmerService.getApplication(userId(authentication))));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<FarmerProfileResponse>> getProfile(
            Authentication authentication) {
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(),
                "Farmer profile fetched successfully", farmerService.getProfile(userId(authentication))));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<FarmerProfileResponse>> updateProfile(
            Authentication authentication,
            @Valid @RequestBody FarmerApplicationRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(),
                "Farmer profile updated successfully", farmerService.updateProfile(userId(authentication), request)));
    }

    private UUID userId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }
}
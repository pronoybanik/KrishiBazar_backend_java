package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FarmerApplicationRequest(
        @NotBlank @Size(max = 150) String farmName,
        @NotBlank @Size(max = 500) String farmAddress,
        @NotBlank @Size(max = 30) String phoneNumber,
        @Size(max = 1000) String description
) {
}
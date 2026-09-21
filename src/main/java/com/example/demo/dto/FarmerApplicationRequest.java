package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FarmerApplicationRequest(
        @NotBlank @Size(max = 150) String farmName,
        @NotNull @Valid Address address,
        @NotBlank @Size(max = 30) String phoneNumber,
        @Size(max = 1000) String description
) {
}
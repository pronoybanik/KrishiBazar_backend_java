package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NidCardImages(
        @NotBlank @Size(max = 500) String frontImage,
        @NotBlank @Size(max = 500) String backImage
) {}

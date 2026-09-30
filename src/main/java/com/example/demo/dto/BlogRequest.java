package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BlogRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank String content,
        @Size(max = 500) String imageUrl
) {
}

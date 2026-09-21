package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Address(
        @NotBlank @Size(max = 100) String district,
        @NotBlank @Size(max = 100) String zilla,
        @NotBlank @Size(max = 500) String detailsAddress
) {
}

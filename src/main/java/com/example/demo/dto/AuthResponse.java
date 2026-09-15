package com.example.demo.dto;

import java.util.UUID;

public record AuthResponse(
        UUID id,
        String name,
        String email,
        String role,
        Boolean active,
        String message
) {
}

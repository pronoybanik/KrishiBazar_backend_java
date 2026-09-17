package com.example.demo.dto;

import java.util.UUID;

public record AuthData(
        UUID id,
        String name,
        String email,
        String role,
        String token
) {
}
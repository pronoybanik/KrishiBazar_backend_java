package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AddressResponse(UUID id, String userName, String mobileNumber, String district, String zilla, String detailsAddress,
        LocalDateTime createdAt, LocalDateTime updatedAt) {}

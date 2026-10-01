package com.example.demo.dto;

import java.util.UUID;

public record FarmerDetailsResponse(UUID id, String name, String email, String farmName,
        Address address, String phoneNumber, String description) {}

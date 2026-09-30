package com.example.demo.dto;

import java.util.UUID;

/** Public farmer information included with a product response. */
public record ProductFarmerResponse(
        UUID id,
        String name,
        String email,
        String farmName,
        Address address,
        String phoneNumber,
        String description
) {
}

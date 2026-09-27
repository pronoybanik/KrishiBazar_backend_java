package com.example.demo.dto;

import java.util.UUID;
import jakarta.validation.constraints.*;

public record OrderRequest(@NotNull UUID addressId, @NotBlank @Pattern(regexp = "COD|BKASH") String paymentMethod,
        String paymentReference) {}

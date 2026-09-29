package com.example.demo.dto;

import java.util.UUID;
import java.util.List;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import com.fasterxml.jackson.annotation.JsonAlias;

public record OrderRequest(@NotNull UUID addressId, @NotBlank @Pattern(regexp = "COD|BKASH") String paymentMethod,
        String paymentReference, @NotEmpty @JsonAlias({"products", "orderItems"}) List<@Valid OrderItemRequest> items) {}

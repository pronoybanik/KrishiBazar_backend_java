package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.validation.constraints.*;

public record CartRequest(@NotNull UUID productId, @NotNull @DecimalMin("0.01") BigDecimal quantity) {}

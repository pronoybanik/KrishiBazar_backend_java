package com.example.demo.dto;
import java.util.UUID; import jakarta.validation.constraints.*;
public record ReportRequest(@NotBlank @Size(max=30) String targetType, UUID targetId, @NotBlank @Size(max=100) String reason, @Size(max=2000) String description) {}

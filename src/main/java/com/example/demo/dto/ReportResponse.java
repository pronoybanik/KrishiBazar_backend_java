package com.example.demo.dto;
import java.time.LocalDateTime; import java.util.UUID;
public record ReportResponse(UUID id, UUID reporterId, String reporterName, String targetType, UUID targetId, String reason, String description, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {}

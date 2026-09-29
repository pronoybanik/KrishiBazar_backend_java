package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name = "reports") @Getter @Setter @NoArgsConstructor
public class Report {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(optional = false) @JoinColumn(name = "reporter_id") private User reporter;
    @Column(nullable = false, length = 30) private String targetType;
    private UUID targetId;
    @Column(nullable = false, length = 100) private String reason;
    @Column(length = 2000) private String description;
    @Column(nullable = false, length = 20) private String status;
    @Column(nullable = false) private LocalDateTime createdAt;
    @Column(nullable = false) private LocalDateTime updatedAt;
    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); updatedAt = createdAt; }
    @PreUpdate void onUpdate() { updatedAt = LocalDateTime.now(); }
}

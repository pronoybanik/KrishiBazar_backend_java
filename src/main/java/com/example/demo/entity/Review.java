package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reviews", uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "user_id"}))
@Getter @Setter @NoArgsConstructor
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(optional = false) @JoinColumn(name = "product_id") private Product product;
    @ManyToOne(optional = false) @JoinColumn(name = "user_id") private User user;
    @Column(nullable = false) private Integer rating;
    @Column(length = 2000) private String comment;
    @Column(nullable = false) private LocalDateTime createdAt;
    @Column(nullable = false) private LocalDateTime updatedAt;
    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); updatedAt = createdAt; }
    @PreUpdate void onUpdate() { updatedAt = LocalDateTime.now(); }
}

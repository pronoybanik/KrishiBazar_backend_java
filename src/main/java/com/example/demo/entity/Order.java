package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter @Setter @NoArgsConstructor
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(optional = false) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Column(nullable = false, length = 30) private String status;
    @Column(nullable = false, length = 20) private String paymentMethod;
    @Column(length = 100) private String paymentReference;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal totalAmount;
    @Column(nullable = false, length = 100) private String district;
    @Column(nullable = false, length = 100) private String zilla;
    @Column(nullable = false, length = 500) private String detailsAddress;
    @Column(length = 150) private String addressUserName;
    @Column(length = 30) private String addressMobileNumber;
    @Column(nullable = false) private LocalDateTime createdAt;
    @Column(nullable = false) private LocalDateTime updatedAt;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); updatedAt = createdAt; }
    @PreUpdate void onUpdate() { updatedAt = LocalDateTime.now(); }
}

package com.example.demo.entity;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cart_items", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
@Getter @Setter @NoArgsConstructor
public class CartItem {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(optional = false) @JoinColumn(name = "user_id", nullable = false) private User user;
    @ManyToOne(optional = false) @JoinColumn(name = "product_id", nullable = false) private Product product;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal quantity;
}

package com.example.demo.entity;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter @Setter @NoArgsConstructor
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(optional = false) @JoinColumn(name = "order_id", nullable = false) private Order order;
    @ManyToOne(optional = false) @JoinColumn(name = "product_id", nullable = false) private Product product;
    @ManyToOne(optional = false) @JoinColumn(name = "farmer_id", nullable = false) private User farmer;
    @Column(nullable = false, length = 150) private String productName;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal unitPrice;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal quantity;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal lineTotal;
}

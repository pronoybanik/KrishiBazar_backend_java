package com.example.demo.repository;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.Order;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findAllByUserIdOrderByCreatedAtDesc(UUID userId);
    List<Order> findDistinctByItemsFarmerIdOrderByCreatedAtDesc(UUID farmerId);
}

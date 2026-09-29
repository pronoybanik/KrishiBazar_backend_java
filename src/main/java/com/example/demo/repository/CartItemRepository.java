package com.example.demo.repository;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    List<CartItem> findAllByUserId(UUID userId);
    Optional<CartItem> findByUserIdAndProductId(UUID userId, UUID productId);
}

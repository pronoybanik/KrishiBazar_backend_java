package com.example.demo.repository;
import java.util.*; import org.springframework.data.jpa.repository.JpaRepository; import com.example.demo.entity.Review;
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findAllByProductIdOrderByCreatedAtDesc(UUID productId);
    Optional<Review> findByProductIdAndUserId(UUID productId, UUID userId);
    long countByProductId(UUID productId);
}

package com.example.demo.repository;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.AddressEntity;

public interface AddressRepository extends JpaRepository<AddressEntity, UUID> {
    List<AddressEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);
    Optional<AddressEntity> findByIdAndUserId(UUID id, UUID userId);
}

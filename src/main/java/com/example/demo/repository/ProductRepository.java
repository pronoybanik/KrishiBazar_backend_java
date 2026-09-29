package com.example.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Product;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findAllByOrderByCreatedAtDesc();

    List<Product> findAllByFarmerIdOrderByCreatedAtDesc(UUID farmerId);
    
    boolean existsByCategoryId(UUID categoryId);

    long countByFarmerId(UUID farmerId);
}

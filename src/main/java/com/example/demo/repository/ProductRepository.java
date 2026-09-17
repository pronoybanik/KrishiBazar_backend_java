package com.example.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Product;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findAllByOrderByCreatedAtDesc();

    List<Product> findAllByFarmerIdOrderByCreatedAtDesc(UUID farmerId);
}
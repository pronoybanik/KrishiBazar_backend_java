package com.example.demo.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.FarmerApplication;

public interface FarmerApplicationRepository extends JpaRepository<FarmerApplication, UUID> {

    Optional<FarmerApplication> findByUserId(UUID userId);

    List<FarmerApplication> findAllByOrderByCreatedAtDesc();
}
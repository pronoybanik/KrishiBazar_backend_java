package com.example.demo.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.FarmerProfile;

public interface FarmerProfileRepository extends JpaRepository<FarmerProfile, UUID> {

    Optional<FarmerProfile> findByUserId(UUID userId);
}
package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import com.example.demo.dto.FarmerApplicationRequest;
import com.example.demo.dto.FarmerApplicationResponse;
import com.example.demo.dto.FarmerProfileResponse;

public interface FarmerService {

    FarmerApplicationResponse apply(UUID userId, FarmerApplicationRequest request);

    FarmerApplicationResponse getApplication(UUID userId);

    FarmerProfileResponse getProfile(UUID userId);

    FarmerProfileResponse updateProfile(UUID userId, FarmerApplicationRequest request);

    List<FarmerApplicationResponse> getApplications(UUID adminId);

    FarmerApplicationResponse approveApplication(UUID adminId, UUID applicationId);

    FarmerApplicationResponse rejectApplication(UUID adminId, UUID applicationId);
}
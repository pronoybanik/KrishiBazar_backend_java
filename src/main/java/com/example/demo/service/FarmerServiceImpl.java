package com.example.demo.service;

import com.example.demo.dto.FarmerApplicationRequest;
import com.example.demo.dto.FarmerApplicationResponse;
import com.example.demo.dto.FarmerProfileResponse;
import com.example.demo.entity.FarmerApplication;
import com.example.demo.entity.FarmerProfile;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.FarmerApplicationRepository;
import com.example.demo.repository.FarmerProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FarmerServiceImpl implements FarmerService {

    private final ActorService actorService;
    private final FarmerApplicationRepository applicationRepository;
    private final FarmerProfileRepository profileRepository;

    public FarmerServiceImpl(ActorService actorService,
            FarmerApplicationRepository applicationRepository,
            FarmerProfileRepository profileRepository) {
        this.actorService = actorService;
        this.applicationRepository = applicationRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public FarmerApplicationResponse apply(UUID userId, FarmerApplicationRequest request) {
        User user = actorService.requireUser(userId);
        if ("FARMER".equals(user.getRole())) {
            throw new ResourceAlreadyExistsException("User is already an approved farmer");
        }

        FarmerApplication application = applicationRepository.findByUserId(userId).orElseGet(FarmerApplication::new);
        if (application.getStatus() == FarmerApplication.Status.PENDING) {
            throw new ResourceAlreadyExistsException("Farmer application is already pending");
        }
        application.setUser(user);
        copyApplicationFields(application, request);
        application.setStatus(FarmerApplication.Status.PENDING);
        return toApplicationResponse(applicationRepository.save(application));
    }

    @Override
    @Transactional(readOnly = true)
    public FarmerApplicationResponse getApplication(UUID userId) {
        actorService.requireUser(userId);
        return applicationRepository.findByUserId(userId)
                .map(this::toApplicationResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer application not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public FarmerProfileResponse getProfile(UUID userId) {
        actorService.requireRole(userId, "FARMER");
        return profileRepository.findByUserId(userId)
                .map(this::toProfileResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer profile not found"));
    }

    @Override
    @Transactional
    public FarmerProfileResponse updateProfile(UUID userId, FarmerApplicationRequest request) {
        User user = actorService.requireRole(userId, "FARMER");
        FarmerProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer profile not found"));
        profile.setUser(user);
        profile.setFarmName(request.farmName().trim());
        profile.setFarmAddress(request.farmAddress().trim());
        profile.setPhoneNumber(request.phoneNumber().trim());
        profile.setDescription(request.description());
        return toProfileResponse(profileRepository.save(profile));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FarmerApplicationResponse> getApplications(UUID adminId) {
        actorService.requireRole(adminId, "ADMIN");
        return applicationRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toApplicationResponse)
                .toList();
    }

    @Override
    @Transactional
    public FarmerApplicationResponse approveApplication(UUID adminId, UUID applicationId) {
        actorService.requireRole(adminId, "ADMIN");
        FarmerApplication application = findApplication(applicationId);
        if (application.getStatus() != FarmerApplication.Status.PENDING) {
            throw new IllegalStateException("Only pending applications can be approved");
        }

        User user = application.getUser();
        user.setRole("FARMER");
        FarmerProfile profile = profileRepository.findByUserId(user.getId()).orElseGet(FarmerProfile::new);
        profile.setUser(user);
        profile.setFarmName(application.getFarmName());
        profile.setFarmAddress(application.getFarmAddress());
        profile.setPhoneNumber(application.getPhoneNumber());
        profile.setDescription(application.getDescription());
        profileRepository.save(profile);

        application.setStatus(FarmerApplication.Status.APPROVED);
        return toApplicationResponse(applicationRepository.save(application));
    }

    @Override
    @Transactional
    public FarmerApplicationResponse rejectApplication(UUID adminId, UUID applicationId) {
        actorService.requireRole(adminId, "ADMIN");
        FarmerApplication application = findApplication(applicationId);
        if (application.getStatus() != FarmerApplication.Status.PENDING) {
            throw new IllegalStateException("Only pending applications can be rejected");
        }
        application.setStatus(FarmerApplication.Status.REJECTED);
        return toApplicationResponse(applicationRepository.save(application));
    }

    private FarmerApplication findApplication(UUID applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer application not found"));
    }

    private void copyApplicationFields(FarmerApplication application, FarmerApplicationRequest request) {
        application.setFarmName(request.farmName().trim());
        application.setFarmAddress(request.farmAddress().trim());
        application.setPhoneNumber(request.phoneNumber().trim());
        application.setDescription(request.description());
    }

    private FarmerApplicationResponse toApplicationResponse(FarmerApplication application) {
        return new FarmerApplicationResponse(application.getId(), application.getUser().getId(),
                application.getFarmName(), application.getFarmAddress(), application.getPhoneNumber(),
                application.getDescription(), application.getStatus(), application.getCreatedAt(), application.getUpdatedAt());
    }

    private FarmerProfileResponse toProfileResponse(FarmerProfile profile) {
        User user = profile.getUser();
        return new FarmerProfileResponse(profile.getId(), user.getId(), user.getName(), user.getEmail(),
                profile.getFarmName(), profile.getFarmAddress(), profile.getPhoneNumber(), profile.getDescription(),
                profile.getCreatedAt(), profile.getUpdatedAt());
    }
}
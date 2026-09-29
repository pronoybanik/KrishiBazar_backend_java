package com.example.demo.service;
import java.util.*; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.*; import com.example.demo.entity.*; import com.example.demo.exception.*; import com.example.demo.repository.*;
@Service public class ReportService {
    private final ActorService actors; private final UserRepository users; private final ReportRepository reports;
    public ReportService(ActorService actors, UserRepository users, ReportRepository reports) { this.actors=actors;this.users=users;this.reports=reports; }
    @Transactional public ReportResponse create(UUID userId, ReportRequest q) { User u=actors.requireUser(userId); Report r=new Report();r.setReporter(u);r.setTargetType(q.targetType().trim().toUpperCase());r.setTargetId(q.targetId());r.setReason(q.reason().trim());r.setDescription(q.description());r.setStatus("OPEN");return response(reports.save(r)); }
    @Transactional(readOnly=true) public List<ReportResponse> all(UUID adminId) { actors.requireRole(adminId,"ADMIN"); return reports.findAllByOrderByCreatedAtDesc().stream().map(this::response).toList(); }
    @Transactional public ReportResponse status(UUID adminId, UUID id, ReportStatusRequest q) { actors.requireRole(adminId,"ADMIN"); Report r=reports.findById(id).orElseThrow(()->new ResourceNotFoundException("Report not found")); String s=q.status().trim().toUpperCase(); if(!Set.of("OPEN","IN_REVIEW","RESOLVED","REJECTED").contains(s)) throw new IllegalArgumentException("Invalid report status");r.setStatus(s);return response(reports.save(r)); }
    private ReportResponse response(Report r) { return new ReportResponse(r.getId(),r.getReporter().getId(),r.getReporter().getName(),r.getTargetType(),r.getTargetId(),r.getReason(),r.getDescription(),r.getStatus(),r.getCreatedAt(),r.getUpdatedAt()); }
}

package com.example.demo.repository;
import java.util.*; import org.springframework.data.jpa.repository.JpaRepository; import com.example.demo.entity.Report;
public interface ReportRepository extends JpaRepository<Report, UUID> { long countByStatus(String status); List<Report> findAllByOrderByCreatedAtDesc(); }

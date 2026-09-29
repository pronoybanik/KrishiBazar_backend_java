package com.example.demo.controller;
import java.util.*; import org.springframework.http.*; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import jakarta.validation.Valid; import com.example.demo.dto.*; import com.example.demo.service.ReportService;
@RestController @RequestMapping("/api/v1/reports") public class ReportController {
 private final ReportService service; public ReportController(ReportService s){service=s;}
 @PostMapping public ResponseEntity<ApiResponse<ReportResponse>> create(Authentication a,@Valid @RequestBody ReportRequest q){return ResponseEntity.status(201).body(new ApiResponse<>(true,201,"Report submitted successfully",service.create(id(a),q)));}
 @GetMapping public ResponseEntity<ApiResponse<List<ReportResponse>>> all(Authentication a){return ResponseEntity.ok(new ApiResponse<>(true,200,"Reports fetched successfully",service.all(id(a))));}
 @PatchMapping("/{reportId}") public ResponseEntity<ApiResponse<ReportResponse>> status(Authentication a,@PathVariable UUID reportId,@Valid @RequestBody ReportStatusRequest q){return ResponseEntity.ok(new ApiResponse<>(true,200,"Report status updated successfully",service.status(id(a),reportId,q)));}
 private UUID id(Authentication a){return UUID.fromString(a.getName());}
}

package com.example.demo.controller;
import java.util.*; import org.springframework.http.*; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import jakarta.validation.Valid; import com.example.demo.dto.*; import com.example.demo.service.ReviewService;
@RestController @RequestMapping("/api/v1/reviews") public class ReviewManagementController {
 private final ReviewService service; public ReviewManagementController(ReviewService s){service=s;}
 @PutMapping("/{reviewId}") public ResponseEntity<ApiResponse<ReviewResponse>> update(Authentication a,@PathVariable UUID reviewId,@Valid @RequestBody ReviewRequest q){return ResponseEntity.ok(new ApiResponse<>(true,200,"Review updated successfully",service.update(id(a),reviewId,q)));}
 @DeleteMapping("/{reviewId}") public ResponseEntity<ApiResponse<Void>> delete(Authentication a,@PathVariable UUID reviewId){service.delete(id(a),reviewId);return ResponseEntity.ok(new ApiResponse<>(true,200,"Review deleted successfully",null));}
 private UUID id(Authentication a){return UUID.fromString(a.getName());}
}

package com.example.demo.controller;
import java.util.*; import org.springframework.http.*; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import jakarta.validation.Valid; import com.example.demo.dto.*; import com.example.demo.service.ReviewService;
@RestController @RequestMapping("/api/v1/products/{productId}/reviews") public class ReviewController {
 private final ReviewService service; public ReviewController(ReviewService s){service=s;}
 @GetMapping public ResponseEntity<ApiResponse<List<ReviewResponse>>> get(@PathVariable UUID productId){return ResponseEntity.ok(new ApiResponse<>(true,200,"Reviews fetched successfully",service.get(productId)));}
 @PostMapping public ResponseEntity<ApiResponse<ReviewResponse>> create(Authentication a,@PathVariable UUID productId,@Valid @RequestBody ReviewRequest q){return ResponseEntity.status(201).body(new ApiResponse<>(true,201,"Review submitted successfully",service.create(id(a),productId,q)));}
 private UUID id(Authentication a){return UUID.fromString(a.getName());}
}

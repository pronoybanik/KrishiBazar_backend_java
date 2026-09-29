package com.example.demo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAll(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID farmerId,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice) {
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(),
                "Products fetched successfully", productService.getAll(q, categoryId, farmerId, minPrice, maxPrice)));
    }

    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllByFarmer(
            @PathVariable UUID farmerId) {
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(),
                "Farmer products fetched successfully", productService.getAllByFarmerId(farmerId)));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable UUID productId) {
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(),
                "Product fetched successfully", productService.getById(productId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            Authentication authentication,
            @Valid @RequestBody ProductRequest request) {
        int statusCode = HttpStatus.CREATED.value();
        return ResponseEntity.status(statusCode).body(new ApiResponse<>(true, statusCode,
                "Product added successfully", productService.create(userId(authentication), request)));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            Authentication authentication,
            @PathVariable UUID productId,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(),
                "Product updated successfully", productService.update(userId(authentication), productId, request)));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            Authentication authentication,
            @PathVariable UUID productId) {
        productService.delete(userId(authentication), productId);
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(),
                "Product deleted successfully", null));
    }

    private UUID userId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }
}

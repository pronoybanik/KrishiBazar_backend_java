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
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CategoryRequest;
import com.example.demo.dto.CategoryResponse;
import com.example.demo.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll() {
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(),
                "Categories fetched successfully", categoryService.getAll()));
    }

    @GetMapping("/{parentCategoryId}/subcategories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getSubcategories(
            @PathVariable UUID parentCategoryId) {
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(),
                "Subcategories fetched successfully", categoryService.getSubcategories(parentCategoryId)));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(),
                "Category fetched successfully", categoryService.getById(categoryId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(
            Authentication authentication,
            @Valid @RequestBody CategoryRequest request) {
        int statusCode = HttpStatus.CREATED.value();
        return ResponseEntity.status(statusCode).body(new ApiResponse<>(true, statusCode,
                "Category created successfully", categoryService.create(userId(authentication), request)));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            Authentication authentication,
            @PathVariable UUID categoryId,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(),
                "Category updated successfully",
                categoryService.update(userId(authentication), categoryId, request)));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            Authentication authentication,
            @PathVariable UUID categoryId) {
        categoryService.delete(userId(authentication), categoryId);
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(),
                "Category deleted successfully", null));
    }

    private UUID userId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }
}

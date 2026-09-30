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
import com.example.demo.dto.BlogRequest;
import com.example.demo.dto.BlogResponse;
import com.example.demo.service.BlogService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/blogs")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BlogResponse>>> getAll() {
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "Blogs fetched successfully", blogService.getAll()));
    }

    @GetMapping("/{blogId}")
    public ResponseEntity<ApiResponse<BlogResponse>> getById(@PathVariable UUID blogId) {
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "Blog fetched successfully", blogService.getById(blogId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BlogResponse>> create(Authentication authentication,
            @Valid @RequestBody BlogRequest request) {
        int statusCode = HttpStatus.CREATED.value();
        return ResponseEntity.status(statusCode).body(new ApiResponse<>(true, statusCode,
                "Blog created successfully", blogService.create(userId(authentication), request)));
    }

    @PutMapping("/{blogId}")
    public ResponseEntity<ApiResponse<BlogResponse>> update(Authentication authentication,
            @PathVariable UUID blogId, @Valid @RequestBody BlogRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "Blog updated successfully",
                blogService.update(userId(authentication), blogId, request)));
    }

    @DeleteMapping("/{blogId}")
    public ResponseEntity<ApiResponse<Void>> delete(Authentication authentication, @PathVariable UUID blogId) {
        blogService.delete(userId(authentication), blogId);
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "Blog deleted successfully", null));
    }

    private UUID userId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }
}

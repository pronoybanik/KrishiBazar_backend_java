package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import com.example.demo.dto.CategoryRequest;
import com.example.demo.dto.CategoryResponse;

public interface CategoryService {

    List<CategoryResponse> getAll();

    CategoryResponse getById(UUID categoryId);

    CategoryResponse create(UUID adminId, CategoryRequest request);

    CategoryResponse update(UUID adminId, UUID categoryId, CategoryRequest request);

    void delete(UUID adminId, UUID categoryId);
}

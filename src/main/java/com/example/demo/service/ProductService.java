package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;

public interface ProductService {

    ProductResponse create(UUID farmerId, ProductRequest request);

    ProductResponse update(UUID farmerId, UUID productId, ProductRequest request);

    void delete(UUID farmerId, UUID productId);

    List<ProductResponse> getAll();
}
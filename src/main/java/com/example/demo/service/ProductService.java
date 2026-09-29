package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;

public interface ProductService {

    ProductResponse create(UUID farmerId, ProductRequest request);

    ProductResponse update(UUID farmerId, UUID productId, ProductRequest request);

    void delete(UUID farmerId, UUID productId);

    List<ProductResponse> getAll(String q, UUID categoryId, UUID farmerId,
            java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice);

    default List<ProductResponse> getAll() { return getAll(null, null, null, null, null); }

    List<ProductResponse> getAllByFarmerId(UUID farmerId);

    ProductResponse getById(UUID productId);

    ProductResponse adminUpdate(UUID adminId, UUID productId, ProductRequest request);
    void adminDelete(UUID adminId, UUID productId);
}

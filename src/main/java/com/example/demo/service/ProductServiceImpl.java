package com.example.demo.service;

import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ActorService actorService;
    private final ProductRepository productRepository;

    public ProductServiceImpl(ActorService actorService, ProductRepository productRepository) {
        this.actorService = actorService;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ProductResponse create(UUID farmerId, ProductRequest request) {
        User farmer = actorService.requireRole(farmerId, "FARMER");
        Product product = new Product();
        product.setFarmer(farmer);
        copyFields(product, request);
        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse update(UUID farmerId, UUID productId, ProductRequest request) {
        User farmer = actorService.requireRole(farmerId, "FARMER");
        Product product = findProduct(productId);
        requireOwner(product, farmer);
        copyFields(product, request);
        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void delete(UUID farmerId, UUID productId) {
        User farmer = actorService.requireRole(farmerId, "FARMER");
        Product product = findProduct(productId);
        requireOwner(product, farmer);
        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAll() {
        return productRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    private Product findProduct(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    private void requireOwner(Product product, User farmer) {
        if (!product.getFarmer().getId().equals(farmer.getId())) {
            throw new ForbiddenException("You can only manage your own products");
        }
    }

    private void copyFields(Product product, ProductRequest request) {
        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setQuantity(request.quantity());
        product.setUnit(request.unit().trim());
        product.setImageUrl(request.imageUrl());
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getFarmer().getId(), product.getFarmer().getName(),
                product.getName(), product.getDescription(), product.getPrice(), product.getQuantity(),
                product.getUnit(), product.getImageUrl(), product.getCreatedAt(), product.getUpdatedAt());
    }
}
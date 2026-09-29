package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

    private final ActorService actorService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ActorService actorService, ProductRepository productRepository,
            CategoryRepository categoryRepository) {
        this.actorService = actorService;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public ProductResponse create(UUID farmerId, ProductRequest request) {
        User farmer = actorService.requireRole(farmerId, "FARMER");
        Product product = new Product();
        product.setFarmer(farmer);
        product.setCategory(findCategory(request.categoryId()));
        copyFields(product, request);
        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse update(UUID farmerId, UUID productId, ProductRequest request) {
        User farmer = actorService.requireRole(farmerId, "FARMER");
        Product product = findProduct(productId);
        requireOwner(product, farmer);
        product.setCategory(findCategory(request.categoryId()));
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
    public List<ProductResponse> getAll(String q, UUID categoryId, UUID farmerId,
            java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice) {
        String query = q == null ? "" : q.trim().toLowerCase();
        return productRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(p -> query.isBlank() || p.getName().toLowerCase().contains(query)
                        || (p.getDescription() != null && p.getDescription().toLowerCase().contains(query)))
                .filter(p -> categoryId == null || p.getCategory().getId().equals(categoryId))
                .filter(p -> farmerId == null || p.getFarmer().getId().equals(farmerId))
                .filter(p -> minPrice == null || p.getPrice().compareTo(minPrice) >= 0)
                .filter(p -> maxPrice == null || p.getPrice().compareTo(maxPrice) <= 0)
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProductResponse adminUpdate(UUID adminId, UUID productId, ProductRequest request) {
        actorService.requireRole(adminId, "ADMIN");
        Product product = findProduct(productId);
        product.setCategory(findCategory(request.categoryId()));
        copyFields(product, request);
        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void adminDelete(UUID adminId, UUID productId) {
        actorService.requireRole(adminId, "ADMIN");
        productRepository.delete(findProduct(productId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllByFarmerId(UUID farmerId) {
        return productRepository.findAllByFarmerIdOrderByCreatedAtDesc(farmerId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(UUID productId) {
        return toResponse(findProduct(productId));
    }

    private Product findProduct(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    private Category findCategory(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
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
        Category category = product.getCategory();
        return new ProductResponse(product.getId(), product.getFarmer().getId(), product.getFarmer().getName(),
            category.getId(), category.getName(),
            category.getParentCategory() == null ? null : category.getParentCategory().getId(),
                product.getName(), product.getDescription(), product.getPrice(), product.getQuantity(),
                product.getUnit(), product.getImageUrl(), product.getCreatedAt(), product.getUpdatedAt());
    }
}

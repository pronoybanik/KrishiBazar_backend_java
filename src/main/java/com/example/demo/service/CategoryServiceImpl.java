package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.CategoryRequest;
import com.example.demo.dto.CategoryResponse;
import com.example.demo.entity.Category;
import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final ActorService actorService;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryServiceImpl(ActorService actorService, CategoryRepository categoryRepository,
            ProductRepository productRepository) {
        this.actorService = actorService;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAllByOrderByNameAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getSubcategories(UUID parentCategoryId) {
        findCategory(parentCategoryId);
        return categoryRepository.findAllByParentCategoryIdOrderByNameAsc(parentCategoryId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(UUID categoryId) {
        return toResponse(findCategory(categoryId));
    }

    @Override
    @Transactional
    public CategoryResponse create(UUID adminId, CategoryRequest request) {
        actorService.requireRole(adminId, "ADMIN");
        Category parentCategory = findParent(request.parentCategoryId());
        validateName(request.name(), parentCategory, null);

        Category category = new Category();
        category.setName(request.name().trim());
        category.setParentCategory(parentCategory);
        return toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse update(UUID adminId, UUID categoryId, CategoryRequest request) {
        actorService.requireRole(adminId, "ADMIN");
        Category category = findCategory(categoryId);
        Category parentCategory = findParent(request.parentCategoryId());

        if (parentCategory != null && parentCategory.getId().equals(categoryId)) {
            throw new ResourceAlreadyExistsException("A category cannot be its own parent");
        }
        if (parentCategory != null && parentCategory.getParentCategory() != null) {
            throw new ResourceAlreadyExistsException("Subcategories cannot have subcategories");
        }
        validateName(request.name(), parentCategory, categoryId);

        category.setName(request.name().trim());
        category.setParentCategory(parentCategory);
        return toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(UUID adminId, UUID categoryId) {
        actorService.requireRole(adminId, "ADMIN");
        Category category = findCategory(categoryId);
        if (categoryRepository.existsByParentCategoryId(categoryId)) {
            throw new ResourceAlreadyExistsException("Delete the category's subcategories first");
        }
        if (productRepository.existsByCategoryId(categoryId)) {
            throw new ResourceAlreadyExistsException("Category is used by products and cannot be deleted");
        }
        categoryRepository.delete(category);
    }

    private Category findCategory(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    private Category findParent(UUID parentCategoryId) {
        if (parentCategoryId == null) {
            return null;
        }
        Category parentCategory = findCategory(parentCategoryId);
        if (parentCategory.getParentCategory() != null) {
            throw new ResourceAlreadyExistsException("Subcategories cannot have subcategories");
        }
        return parentCategory;
    }

    private void validateName(String name, Category parentCategory, UUID categoryId) {
        boolean duplicate;
        if (parentCategory == null) {
            duplicate = categoryId == null
                    ? categoryRepository.existsByNameIgnoreCaseAndParentCategoryIsNull(name.trim())
                    : categoryRepository.existsByNameIgnoreCaseAndParentCategoryIsNullAndIdNot(name.trim(), categoryId);
        } else {
            duplicate = categoryId == null
                    ? categoryRepository.existsByNameIgnoreCaseAndParentCategoryId(name.trim(), parentCategory.getId())
                    : categoryRepository.existsByNameIgnoreCaseAndParentCategoryIdAndIdNot(
                            name.trim(), parentCategory.getId(), categoryId);
        }
        if (duplicate) {
            throw new ResourceAlreadyExistsException("Category name already exists at this level");
        }
    }

    private CategoryResponse toResponse(Category category) {
        Category parent = category.getParentCategory();
        return new CategoryResponse(category.getId(), category.getName(),
                parent == null ? null : parent.getId(),
                parent == null ? null : parent.getName(),
                category.getCreatedAt(), category.getUpdatedAt());
    }
}

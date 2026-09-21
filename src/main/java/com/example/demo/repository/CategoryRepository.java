package com.example.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findAllByOrderByNameAsc();

    boolean existsByNameIgnoreCaseAndParentCategoryId(String name, UUID parentCategoryId);

    boolean existsByNameIgnoreCaseAndParentCategoryIsNull(String name);

    boolean existsByNameIgnoreCaseAndParentCategoryIdAndIdNot(String name, UUID parentCategoryId, UUID id);

    boolean existsByNameIgnoreCaseAndParentCategoryIsNullAndIdNot(String name, UUID id);

    boolean existsByParentCategoryId(UUID parentCategoryId);
}

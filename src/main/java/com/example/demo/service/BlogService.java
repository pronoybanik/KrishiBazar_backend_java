package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import com.example.demo.dto.BlogRequest;
import com.example.demo.dto.BlogResponse;

public interface BlogService {

    List<BlogResponse> getAll();

    BlogResponse getById(UUID blogId);

    BlogResponse create(UUID adminId, BlogRequest request);

    BlogResponse update(UUID adminId, UUID blogId, BlogRequest request);

    void delete(UUID adminId, UUID blogId);
}

package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.BlogRequest;
import com.example.demo.dto.BlogResponse;
import com.example.demo.entity.Blog;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.BlogRepository;

@Service
public class BlogServiceImpl implements BlogService {

    private final ActorService actorService;
    private final BlogRepository blogRepository;

    public BlogServiceImpl(ActorService actorService, BlogRepository blogRepository) {
        this.actorService = actorService;
        this.blogRepository = blogRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogResponse> getAll() {
        return blogRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BlogResponse getById(UUID blogId) {
        return toResponse(findBlog(blogId));
    }

    @Override
    @Transactional
    public BlogResponse create(UUID adminId, BlogRequest request) {
        User admin = actorService.requireRole(adminId, "ADMIN");
        Blog blog = new Blog();
        blog.setAuthor(admin);
        copyFields(blog, request);
        return toResponse(blogRepository.save(blog));
    }

    @Override
    @Transactional
    public BlogResponse update(UUID adminId, UUID blogId, BlogRequest request) {
        actorService.requireRole(adminId, "ADMIN");
        Blog blog = findBlog(blogId);
        copyFields(blog, request);
        return toResponse(blogRepository.save(blog));
    }

    @Override
    @Transactional
    public void delete(UUID adminId, UUID blogId) {
        actorService.requireRole(adminId, "ADMIN");
        blogRepository.delete(findBlog(blogId));
    }

    private Blog findBlog(UUID blogId) {
        return blogRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found"));
    }

    private void copyFields(Blog blog, BlogRequest request) {
        blog.setTitle(request.title().trim());
        blog.setContent(request.content().trim());
        blog.setImageUrl(request.imageUrl());
    }

    private BlogResponse toResponse(Blog blog) {
        return new BlogResponse(blog.getId(), blog.getTitle(), blog.getContent(), blog.getImageUrl(),
                blog.getAuthor().getId(), blog.getAuthor().getName(), blog.getCreatedAt(), blog.getUpdatedAt());
    }
}

package com.example.demo.dto;

public record ApiResponse<T>(
        boolean success,
        int statusCode,
        String message,
        T data
) {
}
package com.example.demo.controller;

import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.*;
import com.example.demo.service.CommerceService;

@RestController
@RequestMapping("/api/v1/farmers/orders")
public class FarmerOrderController {
    private final CommerceService service;
    public FarmerOrderController(CommerceService service) { this.service = service; }
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> orders(Authentication a) { return ResponseEntity.ok(new ApiResponse<>(true, 200, "Farmer orders fetched successfully", service.getFarmerOrders(id(a)))); }
    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<OrderAnalyticsResponse>> analytics(Authentication a) { return ResponseEntity.ok(new ApiResponse<>(true, 200, "Farmer order analytics fetched successfully", service.getFarmerAnalytics(id(a)))); }
    private UUID id(Authentication a) { return UUID.fromString(a.getName()); }
}

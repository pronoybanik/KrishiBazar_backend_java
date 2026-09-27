package com.example.demo.controller;

import java.util.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.*;
import com.example.demo.service.CommerceService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class CommerceController {
    private final CommerceService service;
    public CommerceController(CommerceService service) { this.service = service; }

    @PostMapping("/cart/items")
    public ResponseEntity<ApiResponse<CartItemResponse>> addToCart(Authentication a, @Valid @RequestBody CartRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, 201, "Product added to cart", service.addToCart(id(a), request)));
    }
    @GetMapping("/cart")
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> cart(Authentication a) { return ResponseEntity.ok(new ApiResponse<>(true, 200, "Cart fetched successfully", service.getCart(id(a)))); }
    @DeleteMapping("/cart/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> removeCart(Authentication a, @PathVariable UUID itemId) { service.removeCartItem(id(a), itemId); return ResponseEntity.ok(new ApiResponse<>(true, 200, "Cart item removed", null)); }

    @PostMapping("/addresses")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(Authentication a, @Valid @RequestBody Address request) { return ResponseEntity.status(201).body(new ApiResponse<>(true, 201, "Address added successfully", service.addAddress(id(a), request))); }
    @GetMapping("/addresses")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> addresses(Authentication a) { return ResponseEntity.ok(new ApiResponse<>(true, 200, "Addresses fetched successfully", service.getAddresses(id(a)))); }
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(Authentication a, @PathVariable UUID addressId, @Valid @RequestBody Address request) { return ResponseEntity.ok(new ApiResponse<>(true, 200, "Address updated successfully", service.updateAddress(id(a), addressId, request))); }
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(Authentication a, @PathVariable UUID addressId) { service.deleteAddress(id(a), addressId); return ResponseEntity.ok(new ApiResponse<>(true, 200, "Address deleted successfully", null)); }

    @PostMapping("/orders/confirm")
    public ResponseEntity<ApiResponse<OrderResponse>> confirm(Authentication a, @Valid @RequestBody OrderRequest request) { return ResponseEntity.status(201).body(new ApiResponse<>(true, 201, "Order confirmed successfully", service.confirmOrder(id(a), request))); }
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> myOrders(Authentication a) { return ResponseEntity.ok(new ApiResponse<>(true, 200, "Orders fetched successfully", service.getMyOrders(id(a)))); }
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> order(Authentication a, @PathVariable UUID orderId) { return ResponseEntity.ok(new ApiResponse<>(true, 200, "Order fetched successfully", service.getOrder(id(a), orderId))); }

    private UUID id(Authentication a) { return UUID.fromString(a.getName()); }
}

package com.example.demo.service;

import java.math.BigDecimal;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.exception.*;
import com.example.demo.repository.*;

@Service
public class CommerceService {
    private final ActorService actors;
    private final ProductRepository products;
    private final CartItemRepository carts;
    private final AddressRepository addresses;
    private final OrderRepository orders;
    private final FarmerProfileRepository farmerProfiles;

    public CommerceService(ActorService actors, ProductRepository products, CartItemRepository carts,
            AddressRepository addresses, OrderRepository orders, FarmerProfileRepository farmerProfiles) {
        this.actors = actors; this.products = products; this.carts = carts;
        this.addresses = addresses; this.orders = orders; this.farmerProfiles = farmerProfiles;
    }

    @Transactional
    public CartItemResponse addToCart(UUID userId, CartRequest request) {
        User user = actors.requireUser(userId);
        Product product = products.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        CartItem item = carts.findByUserIdAndProductId(userId, product.getId()).orElseGet(CartItem::new);
        if (item.getId() == null) { item.setUser(user); item.setProduct(product); item.setQuantity(BigDecimal.ZERO); }
        BigDecimal quantity = item.getQuantity().add(request.quantity());
        if (quantity.compareTo(product.getQuantity()) > 0) throw new IllegalArgumentException("Requested quantity exceeds available stock");
        item.setQuantity(quantity);
        return cartResponse(carts.save(item));
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse> getCart(UUID userId) {
        actors.requireUser(userId);
        return carts.findAllByUserId(userId).stream().map(this::cartResponse).toList();
    }

    @Transactional
    public void removeCartItem(UUID userId, UUID itemId) {
        CartItem item = carts.findById(itemId).orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        if (!item.getUser().getId().equals(userId)) throw new ForbiddenException("You can only manage your own cart");
        carts.delete(item);
    }

    @Transactional
    public AddressResponse addAddress(UUID userId, DeliveryAddressRequest request) {
        User user = actors.requireUser(userId);
        AddressEntity address = new AddressEntity(); address.setUser(user); copy(address, request);
        return addressResponse(addresses.save(address));
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses(UUID userId) {
        actors.requireUser(userId);
        return addresses.findAllByUserIdOrderByCreatedAtDesc(userId).stream().map(this::addressResponse).toList();
    }

    @Transactional
    public AddressResponse updateAddress(UUID userId, UUID id, DeliveryAddressRequest request) {
        AddressEntity address = ownAddress(userId, id); copy(address, request);
        return addressResponse(addresses.save(address));
    }

    @Transactional
    public void deleteAddress(UUID userId, UUID id) { addresses.delete(ownAddress(userId, id)); }

    @Transactional
    public OrderResponse confirmOrder(UUID userId, OrderRequest request) {
        User user = actors.requireUser(userId);
        AddressEntity address = ownAddress(userId, request.addressId());
        if (request.items().isEmpty()) throw new IllegalArgumentException("Order must contain at least one item");
        Order order = new Order(); order.setUser(user); order.setStatus("PENDING");
        order.setPaymentMethod(request.paymentMethod().trim().toUpperCase(Locale.ROOT));
        order.setPaymentReference(request.paymentReference());
        order.setDistrict(address.getDistrict()); order.setZilla(address.getZilla()); order.setDetailsAddress(address.getDetailsAddress());
        order.setAddressUserName(address.getUserName()); order.setAddressMobileNumber(address.getMobileNumber());
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest requestedItem : request.items()) {
            Product product = products.findById(requestedItem.productId()).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            if (requestedItem.quantity().compareTo(product.getQuantity()) > 0) throw new IllegalStateException("Insufficient stock for product: " + product.getName());
            product.setQuantity(product.getQuantity().subtract(requestedItem.quantity())); products.save(product);
            OrderItem item = new OrderItem(); item.setOrder(order); item.setProduct(product); item.setFarmer(product.getFarmer());
            item.setProductName(product.getName()); item.setUnitPrice(product.getPrice()); item.setQuantity(requestedItem.quantity());
            item.setLineTotal(product.getPrice().multiply(requestedItem.quantity())); order.getItems().add(item); total = total.add(item.getLineTotal());
        }
        order.setTotalAmount(total); Order saved = orders.save(order);
        carts.deleteAll(carts.findAllByUserId(userId).stream().filter(c -> request.items().stream().anyMatch(i -> i.productId().equals(c.getProduct().getId()))).toList());
        return orderResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID userId, UUID orderId) {
        Order order = findOrder(orderId); if (!order.getUser().getId().equals(userId)) throw new ForbiddenException("You can only view your own order");
        return orderResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(UUID userId) { actors.requireUser(userId); return orders.findAllByUserIdOrderByCreatedAtDesc(userId).stream().map(this::orderResponse).toList(); }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders(UUID adminId) { actors.requireRole(adminId, "ADMIN"); return orders.findAll().stream().sorted(Comparator.comparing(Order::getCreatedAt).reversed()).map(this::orderResponse).toList(); }

    @Transactional(readOnly = true)
    public OrderAnalyticsResponse getAdminAnalytics(UUID adminId) {
        actors.requireRole(adminId, "ADMIN");
        return analytics(orders.findAll(), null);
    }

    @Transactional
    public OrderResponse updateStatus(UUID adminId, UUID orderId, String status) {
        actors.requireRole(adminId, "ADMIN"); Order order = findOrder(orderId); String normalized = status.trim().toUpperCase(Locale.ROOT);
        if (!availableStatuses().contains(normalized)) throw new IllegalArgumentException("Invalid order status");
        order.setStatus(normalized); return orderResponse(orders.save(order));
    }

    @Transactional
    public OrderResponse updateFarmerStatus(UUID farmerId, UUID orderId, String status) {
        actors.requireRole(farmerId, "FARMER"); Order order = findOrder(orderId);
        if (order.getItems().stream().noneMatch(item -> item.getFarmer().getId().equals(farmerId))) throw new ForbiddenException("You can only update orders containing your products");
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        if (!"PICKUP".equals(normalized)) throw new IllegalArgumentException("Farmers can only set an order to PICKUP");
        if (!Set.of("PENDING", "CONFIRMED", "PICKUP").contains(order.getStatus())) throw new IllegalArgumentException("Only pending orders can be marked for pickup");
        order.setStatus(normalized); return orderResponse(orders.save(order), farmerId);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getFarmerOrders(UUID farmerId) { actors.requireRole(farmerId, "FARMER"); return orders.findDistinctByItemsFarmerIdOrderByCreatedAtDesc(farmerId).stream().map(o -> orderResponse(o, farmerId)).toList(); }

    @Transactional(readOnly = true)
    public OrderAnalyticsResponse getFarmerAnalytics(UUID farmerId) {
        actors.requireRole(farmerId, "FARMER");
        return analytics(orders.findDistinctByItemsFarmerIdOrderByCreatedAtDesc(farmerId), farmerId);
    }

    @Transactional(readOnly = true)
    public FarmerDashboardStatsResponse getFarmerDashboard(UUID farmerId) {
        OrderAnalyticsResponse a = getFarmerAnalytics(farmerId);
        long pending = a.ordersByStatus().getOrDefault("PENDING", 0L);
        return new FarmerDashboardStatsResponse(a.totalOrders(), a.deliveredOrders(), pending,
                a.totalSales(), a.deliveredSales(), a.totalItemsSold(), a.ordersByStatus());
    }

    private AddressEntity ownAddress(UUID userId, UUID id) { return addresses.findByIdAndUserId(id, userId).orElseThrow(() -> new ResourceNotFoundException("Address not found")); }
    private Order findOrder(UUID id) { return orders.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found")); }
    private void copy(AddressEntity e, DeliveryAddressRequest a) { e.setUserName(a.userName().trim()); e.setMobileNumber(a.mobileNumber().trim()); e.setDistrict(a.district().trim()); e.setZilla(a.zilla().trim()); e.setDetailsAddress(a.detailsAddress().trim()); }
    private CartItemResponse cartResponse(CartItem i) { BigDecimal total = i.getProduct().getPrice().multiply(i.getQuantity()); return new CartItemResponse(i.getId(), i.getProduct().getId(), i.getProduct().getName(), i.getProduct().getPrice(), i.getQuantity(), total, i.getProduct().getUnit()); }
    private AddressResponse addressResponse(AddressEntity a) { return new AddressResponse(a.getId(), a.getUserName(), a.getMobileNumber(), a.getDistrict(), a.getZilla(), a.getDetailsAddress(), a.getCreatedAt(), a.getUpdatedAt()); }
    private OrderResponse orderResponse(Order o) { return orderResponse(o, null); }
    private OrderResponse orderResponse(Order o, UUID farmerId) { AddressResponse a = new AddressResponse(null, o.getAddressUserName(), o.getAddressMobileNumber(), o.getDistrict(), o.getZilla(), o.getDetailsAddress(), null, null); List<OrderItemResponse> items = o.getItems().stream().filter(i -> farmerId == null || i.getFarmer().getId().equals(farmerId)).map(i -> new OrderItemResponse(i.getProduct().getId(), i.getFarmer().getId(), i.getProductName(), i.getUnitPrice(), i.getQuantity(), i.getLineTotal(), i.getProduct().getUnit(), farmerDetails(i.getFarmer()))).toList(); return new OrderResponse(o.getId(), o.getUser().getId(), o.getUser().getName(), o.getStatus(), o.getPaymentMethod(), o.getPaymentReference(), o.getTotalAmount(), a, items, o.getCreatedAt(), o.getUpdatedAt()); }

    private FarmerDetailsResponse farmerDetails(User farmer) { return farmerProfiles.findByUserId(farmer.getId()).map(profile -> new FarmerDetailsResponse(farmer.getId(), farmer.getName(), farmer.getEmail(), profile.getFarmName(), new Address(profile.getDistrict(), profile.getZilla(), profile.getDetailsAddress()), profile.getPhoneNumber(), profile.getDescription())).orElseGet(() -> new FarmerDetailsResponse(farmer.getId(), farmer.getName(), farmer.getEmail(), null, null, null, null)); }
    private Set<String> availableStatuses() { return Set.of("PENDING", "CONFIRMED", "PICKUP", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"); }

    private OrderAnalyticsResponse analytics(List<Order> orderList, UUID farmerId) {
        long totalOrders = orderList.size();
        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalItemsSold = BigDecimal.ZERO;
        BigDecimal deliveredSales = BigDecimal.ZERO;
        long deliveredOrders = 0;
        Map<String, Long> byStatus = new TreeMap<>();
        Map<String, BigDecimal> byPayment = new TreeMap<>();
        for (Order order : orderList) {
            byStatus.merge(order.getStatus(), 1L, Long::sum);
            BigDecimal orderSales = BigDecimal.ZERO;
            for (OrderItem item : order.getItems()) {
                if (farmerId == null || item.getFarmer().getId().equals(farmerId)) {
                    orderSales = orderSales.add(item.getLineTotal());
                    totalItemsSold = totalItemsSold.add(item.getQuantity());
                }
            }
            totalSales = totalSales.add(orderSales);
            if ("DELIVERED".equals(order.getStatus())) { deliveredSales = deliveredSales.add(orderSales); deliveredOrders++; }
            byPayment.merge(order.getPaymentMethod(), orderSales, BigDecimal::add);
        }
        return new OrderAnalyticsResponse(totalOrders, totalSales, totalItemsSold, byStatus, byPayment, deliveredSales, deliveredOrders);
    }
}

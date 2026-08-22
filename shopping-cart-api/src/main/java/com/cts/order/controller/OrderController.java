package com.cts.order.controller;

import com.cts.common.api.ApiResponse;
import com.cts.common.api.PageResponse;
import com.cts.order.api.OrderResponse;
import com.cts.order.service.OrderService;
import com.cts.security.SecurityPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getMyOrders(
            @AuthenticationPrincipal SecurityPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long customerId = Long.parseLong(principal.userId());
        return ResponseEntity.ok(ApiResponse.ok("Orders fetched successfully", 
                orderService.getCustomerOrders(customerId, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @AuthenticationPrincipal SecurityPrincipal principal,
            @PathVariable Long id) {
        Long customerId = Long.parseLong(principal.userId());
        boolean isAdmin = principal.roles().contains("ADMIN");
        return ResponseEntity.ok(ApiResponse.ok("Order fetched successfully", 
                orderService.getOrderById(customerId, id, isAdmin)));
    }
}
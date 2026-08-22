package com.cts.cart.controller;

import com.cts.cart.api.AddCartItemRequest;
import com.cts.cart.api.CartResponse;
import com.cts.cart.api.UpdateCartItemRequest;
import com.cts.cart.service.CartService;
import com.cts.common.api.ApiResponse;
import com.cts.order.api.OrderResponse;
import com.cts.security.SecurityPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('CUSTOMER')") // Only CUSTOMERS can access cart/checkout endpoints
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@AuthenticationPrincipal SecurityPrincipal principal) {
        Long customerId = Long.parseLong(principal.userId());
        return ResponseEntity.ok(ApiResponse.ok("Cart fetched successfully", cartService.getCart(customerId)));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @AuthenticationPrincipal SecurityPrincipal principal,
            @Valid @RequestBody AddCartItemRequest request) {
        
        Long customerId = Long.parseLong(principal.userId());
        return ResponseEntity.ok(ApiResponse.ok("Item added to cart", cartService.addToCart(customerId, request)));
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateQuantity(
            @AuthenticationPrincipal SecurityPrincipal principal,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        
        Long customerId = Long.parseLong(principal.userId());
        return ResponseEntity.ok(ApiResponse.ok("Cart updated", 
                cartService.updateQuantity(customerId, cartItemId, request)));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> removeCartItem(
            @AuthenticationPrincipal SecurityPrincipal principal,
            @PathVariable Long cartItemId) {
        
        Long customerId = Long.parseLong(principal.userId());
        cartService.removeCartItem(customerId, cartItemId);
        return ResponseEntity.ok(ApiResponse.ok("Item removed from cart", null));
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(@AuthenticationPrincipal SecurityPrincipal principal) {
        Long customerId = Long.parseLong(principal.userId());
        OrderResponse response = cartService.checkout(customerId);
        
        return ResponseEntity.ok(ApiResponse.ok("Order placed successfully", response));
    }
}
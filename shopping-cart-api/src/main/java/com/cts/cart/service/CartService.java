package com.cts.cart.service;

import com.cts.cart.api.AddCartItemRequest;
import com.cts.cart.api.CartResponse;
import com.cts.cart.api.UpdateCartItemRequest;
import com.cts.order.api.OrderResponse;

public interface CartService {
    CartResponse getCart(Long customerId);
    CartResponse addToCart(Long customerId, AddCartItemRequest request);
    CartResponse updateQuantity(Long customerId, Long cartItemId, UpdateCartItemRequest request);
    void removeCartItem(Long customerId, Long cartItemId);
    
    // The critical transactional checkout method
    OrderResponse checkout(Long customerId);
}
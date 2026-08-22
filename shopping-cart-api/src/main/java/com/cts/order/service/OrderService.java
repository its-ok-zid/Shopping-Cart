package com.cts.order.service;

import com.cts.common.api.PageResponse;
import com.cts.order.api.OrderResponse;

public interface OrderService {
    PageResponse<OrderResponse> getCustomerOrders(Long customerId, int page, int size);
    OrderResponse getOrderById(Long customerId, Long orderId, boolean isAdmin);
}
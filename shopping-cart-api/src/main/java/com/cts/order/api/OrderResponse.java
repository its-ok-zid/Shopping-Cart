package com.cts.order.api;

import com.cts.order.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(Long id, String orderNumber, OrderStatus status, BigDecimal total, Instant createdAt,
                            List<OrderItemResponse> items) { }

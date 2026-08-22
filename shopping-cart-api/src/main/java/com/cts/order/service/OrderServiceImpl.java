package com.cts.order.service;

import com.cts.common.api.PageResponse;
import com.cts.common.error.ApiException;
import com.cts.order.api.OrderItemResponse;
import com.cts.order.api.OrderResponse;
import com.cts.order.domain.PurchaseOrder;
import com.cts.order.repository.PurchaseOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final PurchaseOrderRepository orderRepository;

    public OrderServiceImpl(PurchaseOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getCustomerOrders(Long customerId, int page, int size) {
        Page<PurchaseOrder> orders = orderRepository.findByCustomerIdOrderByCreatedAtDesc(
                customerId, PageRequest.of(page, size)
        );
        return PageResponse.from(orders, this::mapToOrderResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long customerId, Long orderId, boolean isAdmin) {
        PurchaseOrder order = isAdmin 
                ? orderRepository.findWithItemsById(orderId).orElseThrow(() -> ApiException.notFound("Order"))
                : orderRepository.findByIdAndCustomerId(orderId, customerId).orElseThrow(() -> ApiException.notFound("Order"));
        return mapToOrderResponse(order);
    }

    private OrderResponse mapToOrderResponse(PurchaseOrder order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(i -> new OrderItemResponse(
                        i.getProductId(), i.getProductName(), i.getQuantity(), i.getUnitPrice(), i.getLineTotal()
                )).toList();

        return new OrderResponse(
                order.getId(), order.getOrderNumber(), order.getStatus(), 
                order.getTotal(), order.getCreatedAt(), itemResponses
        );
    }
}
package com.cts.cart.service;

import com.cts.cart.api.AddCartItemRequest;
import com.cts.cart.api.CartItemResponse;
import com.cts.cart.api.CartResponse;
import com.cts.cart.api.UpdateCartItemRequest;
import com.cts.cart.domain.CartItem;
import com.cts.cart.repository.CartItemRepository;
import com.cts.common.error.ApiException;
import com.cts.order.api.OrderItemResponse;
import com.cts.order.api.OrderResponse;
import com.cts.order.domain.OrderItem;
import com.cts.order.domain.PurchaseOrder;
import com.cts.order.repository.PurchaseOrderRepository;
import com.cts.product.domain.Product;
import com.cts.product.repository.ProductRepository;
import com.cts.user.domain.AppUser;
import com.cts.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PurchaseOrderRepository orderRepository;

    public CartServiceImpl(CartItemRepository cartItemRepository, 
                           ProductRepository productRepository, 
                           UserRepository userRepository, 
                           PurchaseOrderRepository orderRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(Long customerId) {
        List<CartItem> items = cartItemRepository.findByCustomerIdOrderByIdAsc(customerId);
        return mapToCartResponse(items);
    }

    @Override
    @Transactional
    public CartResponse addToCart(Long customerId, AddCartItemRequest request) {
        AppUser customer = userRepository.findById(customerId)
                .orElseThrow(() -> ApiException.notFound("Customer"));

        Product product = productRepository.findByIdAndActiveTrue(request.productId())
                .orElseThrow(() -> ApiException.notFound("Product"));

        // Check if item is already in cart to combine quantities
        CartItem cartItem = cartItemRepository.findByCustomerIdAndProductId(customerId, product.getId())
                .orElse(new CartItem(customer, product, 0));

        int newQuantity = cartItem.getQuantity() + request.quantity();

        // 1. INVENTORY GUARDRAIL: Prevent overselling in the cart
        if (newQuantity > product.getStock()) {
            throw ApiException.conflict("INSUFFICIENT_STOCK", 
                    "Only " + product.getStock() + " units available for " + product.getName());
        }

        cartItem.setQuantity(newQuantity);
        cartItemRepository.save(cartItem);

        return getCart(customerId);
    }

    @Override
    @Transactional
    public CartResponse updateQuantity(Long customerId, Long cartItemId, UpdateCartItemRequest request) {
        CartItem cartItem = cartItemRepository.findByIdAndCustomerId(cartItemId, customerId)
                .orElseThrow(() -> ApiException.notFound("Cart item"));

        if (request.quantity() > cartItem.getProduct().getStock()) {
            throw ApiException.conflict("INSUFFICIENT_STOCK", 
                    "Only " + cartItem.getProduct().getStock() + " units available.");
        }

        cartItem.setQuantity(request.quantity());
        cartItemRepository.save(cartItem);

        return getCart(customerId);
    }

    @Override
    @Transactional
    public void removeCartItem(Long customerId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findByIdAndCustomerId(cartItemId, customerId)
                .orElseThrow(() -> ApiException.notFound("Cart item"));
        cartItemRepository.delete(cartItem);
    }

    @Override
    @Transactional
    public OrderResponse checkout(Long customerId) {
        AppUser customer = userRepository.findById(customerId)
                .orElseThrow(() -> ApiException.notFound("Customer"));

        List<CartItem> cartItems = cartItemRepository.findByCustomerIdOrderByIdAsc(customerId);
        if (cartItems.isEmpty()) {
            throw ApiException.badRequest("CART_EMPTY", "Cannot checkout an empty cart");
        }

        // 2. PESSIMISTIC LOCKING: Lock all products in this cart simultaneously 
        // to prevent other users from checking out the exact same stock at the exact same millisecond.
        List<Long> productIds = cartItems.stream().map(ci -> ci.getProduct().getId()).toList();
        Map<Long, Product> lockedProducts = productRepository.findAllByIdInForUpdate(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        PurchaseOrder order = new PurchaseOrder(customer);

        for (CartItem cartItem : cartItems) {
            Product product = lockedProducts.get(cartItem.getProduct().getId());

            // 3. RE-VALIDATE STOCK: Ensure stock hasn't changed since they added it to the cart
            if (!product.isActive() || cartItem.getQuantity() > product.getStock()) {
                throw ApiException.conflict("INVENTORY_CHANGED", 
                        "Product " + product.getName() + " is out of stock or inactive.");
            }

            // 4. DEDUCT STOCK ATOMICALLY
            product.decrementStock(cartItem.getQuantity());

            // 5. CREATE IMMUTABLE DATA SNAPSHOT (Locking in the price and name at time of purchase)
            OrderItem orderItem = new OrderItem(order, product.getId(), product.getName(), 
                    cartItem.getQuantity(), product.getPrice());
            
            order.addItem(orderItem);
        }

        PurchaseOrder savedOrder = orderRepository.save(order);

        // 6. CLEAR THE CART
        cartItemRepository.deleteAll(cartItems);

        return mapToOrderResponse(savedOrder);
    }

    private CartResponse mapToCartResponse(List<CartItem> items) {
        BigDecimal total = BigDecimal.ZERO;
        List<CartItemResponse> responseItems = items.stream().map(item -> {
            Product p = item.getProduct();
            BigDecimal lineTotal = p.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            boolean available = p.isActive() && item.getQuantity() <= p.getStock();
            
            return new CartItemResponse(
                    item.getId(), p.getId(), p.getName(),
                    p.getThumbnailId() != null ? "/api/products/" + p.getId() + "/thumbnail" : null,
                    p.getPrice(), item.getQuantity(), lineTotal, available
            );
        }).toList();

        for (CartItemResponse item : responseItems) {
            total = total.add(item.lineTotal());
        }

        return new CartResponse(responseItems, total);
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
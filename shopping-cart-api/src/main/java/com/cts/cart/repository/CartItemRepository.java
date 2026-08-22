package com.cts.cart.repository;

import com.cts.cart.domain.CartItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @EntityGraph(attributePaths = "product")
    List<CartItem> findByCustomerIdOrderByIdAsc(Long customerId);
    Optional<CartItem> findByCustomerIdAndProductId(Long customerId, Long productId);
    Optional<CartItem> findByIdAndCustomerId(Long id, Long customerId);
}

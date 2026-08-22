package com.cts.order.repository;

import com.cts.order.domain.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    Page<PurchaseOrder> findByCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);
    @EntityGraph(attributePaths = "items")
    Optional<PurchaseOrder> findByIdAndCustomerId(Long id, Long customerId);
    @EntityGraph(attributePaths = "items")
    Optional<PurchaseOrder> findWithItemsById(Long id);
}

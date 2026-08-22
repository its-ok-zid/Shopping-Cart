package com.cts.order.domain;

import com.cts.user.domain.AppUser;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "purchase_order")
public class PurchaseOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_number", nullable = false, unique = true, length = 40)
    private String orderNumber;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "customer_id", nullable = false)
    private AppUser customer;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30)
    private OrderStatus status;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal total;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    protected PurchaseOrder() { }
    public PurchaseOrder(AppUser customer) {
        this.customer = customer; this.orderNumber = "ORD-" + UUID.randomUUID(); this.status = OrderStatus.CHECKED_OUT;
        this.total = BigDecimal.ZERO; this.createdAt = Instant.now();
    }
    public void addItem(OrderItem item) { items.add(item); total = total.add(item.getLineTotal()); }
    public Long getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public AppUser getCustomer() { return customer; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotal() { return total; }
    public Instant getCreatedAt() { return createdAt; }
    public List<OrderItem> getItems() { return List.copyOf(items); }
}

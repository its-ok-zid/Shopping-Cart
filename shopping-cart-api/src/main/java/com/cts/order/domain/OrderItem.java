package com.cts.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "order_id", nullable = false)
    private PurchaseOrder order;
    @Column(name = "product_id", nullable = false)
    private Long productId;
    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;
    @Column(nullable = false)
    private int quantity;
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;
    @Column(name = "line_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal lineTotal;

    protected OrderItem() { }
    public OrderItem(PurchaseOrder order, Long productId, String productName, int quantity, BigDecimal unitPrice) {
        this.order = order; this.productId = productId; this.productName = productName; this.quantity = quantity;
        this.unitPrice = unitPrice; this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getLineTotal() { return lineTotal; }
}

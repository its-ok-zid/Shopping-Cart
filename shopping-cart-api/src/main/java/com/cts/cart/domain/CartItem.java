package com.cts.cart.domain;

import com.cts.product.domain.Product;
import com.cts.user.domain.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "cart_item", uniqueConstraints = @UniqueConstraint(name = "uk_cart_customer_product", columnNames = {"customer_id", "product_id"}))
public class CartItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "customer_id", nullable = false)
    private AppUser customer;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @Column(nullable = false)
    private int quantity;

    protected CartItem() { }
    public CartItem(AppUser customer, Product product, int quantity) { this.customer = customer; this.product = product; this.quantity = quantity; }
    public Long getId() { return id; }
    public AppUser getCustomer() { return customer; }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

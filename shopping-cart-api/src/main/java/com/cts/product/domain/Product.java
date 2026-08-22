package com.cts.product.domain;

import com.cts.user.domain.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "product", uniqueConstraints = @UniqueConstraint(name = "uk_product_sku", columnNames = "sku"))
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private AppUser seller;

    @Column(nullable = false, length = 100)
    private String sku;
    @Column(nullable = false, length = 255)
    private String name;
    @Column(nullable = false, length = 2000)
    private String description;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;
    @Column(nullable = false)
    private int stock;
    @Column(nullable = false)
    private boolean active = true;
    @Column(name = "thumbnail_id", length = 24)
    private String thumbnailId;
    @Version
    private long version;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Product() { }

    public Product(AppUser seller, String sku, String name, String description, BigDecimal price, int stock) {
        this.seller = seller;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    @PrePersist void onCreate() { createdAt = updatedAt = Instant.now(); }
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }

    public void update(String sku, String name, String description, BigDecimal price, Integer stock) {
        if (sku != null) this.sku = sku;
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        if (price != null) this.price = price;
        if (stock != null) this.stock = stock;
    }
    public void restock(int newStock) { this.stock = newStock; }
    public void deactivate() { this.active = false; }
    public void replaceThumbnail(String thumbnailId) { this.thumbnailId = thumbnailId; }
    public void decrementStock(int amount) { this.stock -= amount; }

    public Long getId() { return id; }
    public AppUser getSeller() { return seller; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public int getStock() { return stock; }
    public boolean isActive() { return active; }
    public String getThumbnailId() { return thumbnailId; }
    public long getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}

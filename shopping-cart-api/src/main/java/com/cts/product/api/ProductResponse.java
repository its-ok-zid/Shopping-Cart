package com.cts.product.api;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(Long id, Long sellerId, String sku, String name, String description, BigDecimal price,
                              int stock, boolean inStock, boolean active, String thumbnailUrl, Instant updatedAt) { }

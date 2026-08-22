package com.cts.cart.api;

import java.math.BigDecimal;

public record CartItemResponse(Long id, Long productId, String productName, String thumbnailUrl, BigDecimal unitPrice,
                               int quantity, BigDecimal lineTotal, boolean available) { }

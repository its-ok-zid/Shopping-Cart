package com.cts.cart.api;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(List<CartItemResponse> items, BigDecimal total) { }

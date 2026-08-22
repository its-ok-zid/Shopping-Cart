package com.cts.cart.api;

import jakarta.validation.constraints.Min;

public record UpdateCartItemRequest(@Min(1) int quantity) { }

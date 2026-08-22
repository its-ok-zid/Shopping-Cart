package com.cts.product.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StockUpdateRequest(@Min(0) int stock, @NotBlank @Size(max = 255) String reason) { }

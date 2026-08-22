package com.cts.product.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank @Pattern(regexp = "^[A-Za-z0-9_-]{3,100}$", message = "must contain 3-100 letters, numbers, _ or -") String sku,
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 2000) String description,
        @NotNull @DecimalMin(value = "0.01") BigDecimal price,
        @Min(0) int stock
) { }

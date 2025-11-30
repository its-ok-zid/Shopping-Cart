package com.cts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponseDTO {
    private Long id;
    private String name;
    private String description;
    private float cost;
    private String mfrNo;
    private int stock;
    private String thumbnailId;
}

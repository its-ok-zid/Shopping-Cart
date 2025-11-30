package com.cts.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating an Item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDTO {

    @NotBlank(message = "name is required")
    @Size(max = 255)
    private String name;

    @Size(max = 1000)
    private String description;

    @Min(value = 0, message = "cost must be >= 0")
    private float cost;

    @Size(max = 100)
    private String mfrNo;

    @Min(value = 0, message = "stock must be >= 0")
    private int stock;

    /**
     * Optional: If you already uploaded a thumbnail to GridFS, you can pass the fileId here.
     * Otherwise you can upload the thumbnail later via POST /api/thumbnails/{itemId}
     */
    private String thumbnailId;
}

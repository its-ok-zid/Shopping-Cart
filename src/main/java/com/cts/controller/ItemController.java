package com.cts.controller;


import com.cts.dto.ItemRequestDTO;
import com.cts.dto.ItemResponseDTO;
import com.cts.exception.ApiResponse;
import com.cts.model.ItemDetails;
import com.cts.repository.ItemRepository;
import com.cts.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemRepository itemRepository;

    private final ItemService itemService;

    public ItemController(ItemRepository itemRepository, ItemService itemService) {
        this.itemRepository = itemRepository;
        this.itemService = itemService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<ItemDetails> getItem(@PathVariable Long id) {
        return itemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping
    public ResponseEntity<ApiResponse<List<ItemResponseDTO>>> getAllItems() {
        return ResponseEntity.ok(
                ApiResponse.<List<ItemResponseDTO>>builder()
                        .success(true)
                        .message("Items fetched successfully")
                        .data(itemService.getAllItems())
                        .build()
        );
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ItemResponseDTO>> createItem(
            @RequestPart("item") @Valid ItemRequestDTO item,
            @RequestPart("thumbnail") MultipartFile thumbnail) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ItemResponseDTO>builder()
                        .success(true)
                        .message("Item created successfully")
                        .data(itemService.createItem(item, thumbnail))
                        .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ItemResponseDTO>> updateItem(
            @PathVariable Long id,
            @RequestPart("item") @Valid ItemRequestDTO item,
            @RequestPart(value = "thumbnail", required = false)
            MultipartFile thumbnail) {

        return ResponseEntity.ok(
                ApiResponse.<ItemResponseDTO>builder()
                        .success(true)
                        .message("Item updated successfully")
                        .data(itemService.updateItem(id, item, thumbnail))
                        .build());
    }
}

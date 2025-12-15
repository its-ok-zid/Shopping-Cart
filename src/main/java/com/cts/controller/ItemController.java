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
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping
    public ResponseEntity<ApiResponse<List<ItemResponseDTO>>> getAllItems() {

        List<ItemResponseDTO> items = itemService.getAllItems();

        return ResponseEntity.ok(
                ApiResponse.<List<ItemResponseDTO>>builder()
                        .success(true)
                        .message("Items fetched successfully")
                        .data(items)
                        .build()
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<ItemDetails> getItem(@PathVariable Long id) {
        return itemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ItemResponseDTO>> createItem(
            @RequestPart("item") @Valid ItemRequestDTO itemRequest,
            @RequestPart("thumbnail") MultipartFile thumbnail) {

        ItemResponseDTO response = itemService.createItem(itemRequest, thumbnail);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ItemResponseDTO>builder()
                        .success(true)
                        .message("Item created successfully")
                        .data(response)
                        .build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> updateItem(@PathVariable Long id, @RequestBody ItemRequestDTO req) {
        var opt = itemRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        ItemDetails e = opt.get();
        e.setName(req.getName());
        e.setItemDescription(req.getDescription());
        e.setItemCost(req.getCost());
        e.setMfrNo(req.getMfrNo());
        e.setStock(req.getStock());
        itemRepository.save(e);
        return ResponseEntity.ok(new ItemResponseDTO(e.getId(), e.getName(), e.getItemDescription(), e.getItemCost(), e.getMfrNo(), e.getStock(), e.getThumbnailId()));
    }
}

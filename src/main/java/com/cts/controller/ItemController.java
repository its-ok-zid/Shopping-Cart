package com.cts.controller;


import com.cts.dto.ItemRequestDTO;
import com.cts.dto.ItemResponseDTO;
import com.cts.model.ItemDetails;
import com.cts.repository.ItemRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemRepository itemRepository;

    public ItemController(ItemRepository itemRepository) { this.itemRepository = itemRepository; }

    // public search/list
    @GetMapping("/{id}")
    public ResponseEntity<ItemDetails> getItem(@PathVariable Long id) {
        return itemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // admin: create
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ItemResponseDTO> createItem(@Valid @RequestBody ItemRequestDTO req) {
        ItemDetails entity = new ItemDetails();
        entity.setName(req.getName());
        entity.setItemDescription(req.getDescription());
        entity.setItemCost(req.getCost());
        entity.setMfrNo(req.getMfrNo());
        entity.setStock(req.getStock());
        ItemDetails saved = itemRepository.save(entity);
        ItemResponseDTO resp = new ItemResponseDTO(saved.getId(), saved.getName(), saved.getItemDescription(),
                saved.getItemCost(), saved.getMfrNo(), saved.getStock(), saved.getThumbnailId());
        return ResponseEntity.status(201).body(resp);
    }

    // admin: update
    @PreAuthorize("hasRole('ADMIN')")
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

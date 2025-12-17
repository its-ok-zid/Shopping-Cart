package com.cts.serviceImpl;

import com.cts.dto.ItemRequestDTO;
import com.cts.dto.ItemResponseDTO;
import com.cts.exception.ItemCreationException;
import com.cts.model.ItemDetails;
import com.cts.repository.ItemRepository;
import com.cts.service.ItemService;
import com.cts.service.ThumbnailService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ThumbnailService thumbnailService;

    public ItemServiceImpl(ItemRepository itemRepository, ThumbnailService thumbnailService) {
        this.itemRepository = itemRepository;
        this.thumbnailService = thumbnailService;
    }
    

    @Override
    public ItemDetails getItemByName(String name) {
        return itemRepository.findItemByName(name);
    }

    @Override
    @Transactional
    public ItemResponseDTO createItem(ItemRequestDTO req,
                                      MultipartFile thumbnail) {

        String thumbnailId = null;

        try {
            // 1️⃣ Upload thumbnail (Mongo)
            thumbnailId = thumbnailService.store(thumbnail);

            if (thumbnailId == null) {
                throw new ItemCreationException("Thumbnail upload failed");
            }

            // 2️⃣ Save item (Postgres)
            ItemDetails item = new ItemDetails();
            item.setName(req.getName());
            item.setItemDescription(req.getDescription());
            item.setItemCost(req.getCost());
            item.setMfrNo(req.getMfrNo());
            item.setStock(req.getStock());
            item.setThumbnailId(thumbnailId);

            ItemDetails saved = itemRepository.save(item);

            // 3️⃣ Success
            return new ItemResponseDTO(
                    saved.getId(),
                    saved.getName(),
                    saved.getItemDescription(),
                    saved.getItemCost(),
                    saved.getMfrNo(),
                    saved.getStock(),
                    saved.getThumbnailId()
            );

        } catch (Exception ex) {

            // 🔁 COMPENSATION LOGIC
            if (thumbnailId != null) {
                thumbnailService.delete(thumbnailId);
            }

            throw new ItemCreationException("Failed to create item with thumbnail", ex);
        }
    }

    @Override
    public List<ItemResponseDTO> getAllItems() {
        return itemRepository.findAll().stream()
                .map(item -> new ItemResponseDTO(
                        item.getId(),
                        item.getName(),
                        item.getItemDescription(),
                        item.getItemCost(),
                        item.getMfrNo(),
                        item.getStock(),
                        item.getThumbnailId()
                ))
                .toList();
    }


    @Override
    @Transactional
    public ItemResponseDTO updateItem(Long itemId,
                                      ItemRequestDTO req,
                                      MultipartFile newThumbnail) {

        ItemDetails item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemCreationException("Item not found"));

        String oldThumbnailId = item.getThumbnailId();
        String newThumbnailId = null;

        try {
            // Upload new thumbnail if provided
            if (newThumbnail != null && !newThumbnail.isEmpty()) {
                newThumbnailId = thumbnailService.store(newThumbnail);
                item.setThumbnailId(newThumbnailId);
            }

            // Update item fields
            item.setName(req.getName());
            item.setItemDescription(req.getDescription());
            item.setItemCost(req.getCost());
            item.setMfrNo(req.getMfrNo());
            item.setStock(req.getStock());

            ItemDetails saved = itemRepository.save(item);

            // Delete old thumbnail only AFTER success
            if (newThumbnailId != null && oldThumbnailId != null) {
                thumbnailService.delete(oldThumbnailId);
            }

            return new ItemResponseDTO(
                    saved.getId(),
                    saved.getName(),
                    saved.getItemDescription(),
                    saved.getItemCost(),
                    saved.getMfrNo(),
                    saved.getStock(),
                    saved.getThumbnailId()
            );

        } catch (Exception ex) {

            // Rollback new thumbnail
            if (newThumbnailId != null) {
                thumbnailService.delete(newThumbnailId);
            }

            throw new ItemCreationException("Failed to update item", ex);
        }
    }


}

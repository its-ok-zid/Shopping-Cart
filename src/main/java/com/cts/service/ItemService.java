package com.cts.service;

import com.cts.dto.ItemRequestDTO;
import com.cts.dto.ItemResponseDTO;
import com.cts.model.ItemDetails;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ItemService {

    List<ItemResponseDTO> getAllItems();

    ItemDetails getItemByName(String name);

    ItemResponseDTO createItem(@Valid ItemRequestDTO itemRequest, MultipartFile thumbnail);

    ItemResponseDTO updateItem(Long itemId, ItemRequestDTO itemRequest, MultipartFile thumbnail);
}

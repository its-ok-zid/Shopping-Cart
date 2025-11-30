package com.cts.controller;

import com.cts.model.ItemDetails;
import com.cts.repository.ItemRepository;
import com.cts.service.ThumbnailService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/thumbnails")
public class ThumbnailController {

    private final ThumbnailService thumbnailService;
    private final ItemRepository itemRepository;

    public ThumbnailController(ThumbnailService thumbnailService, ItemRepository itemRepository) {
        this.thumbnailService = thumbnailService;
        this.itemRepository = itemRepository;
    }

    /**
     * Upload a thumbnail for an item and set thumbnailId on the ItemDetails.
     * Request: multipart/form-data with "file" field.
     */
    @PostMapping("/{itemId}")
    public ResponseEntity<Map<String, String>> uploadThumbnail(@PathVariable Long itemId,
                                                               @RequestParam("file") MultipartFile file) throws IOException {
        // validate item
        ItemDetails item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found: " + itemId));

        // store file
        String fileId = thumbnailService.store(file);
        if (fileId == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store file");
        }

        // set thumbnailId and save item
        item.setThumbnailId(fileId);
        itemRepository.save(item);

        return ResponseEntity.ok(Map.of("fileId", fileId));
    }

    /**
     * Stream thumbnail by fileId.
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<?> getThumbnail(@PathVariable String fileId) throws IOException {
        var resourceOpt = thumbnailService.loadAsResource(fileId);
        if (resourceOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        GridFsResource resource = resourceOpt.get();

        String contentType = resource.getContentType();
        InputStreamResource inputStreamResource = new InputStreamResource(resource.getInputStream());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.inline().filename(resource.getFilename()).build());

        MediaType mediaType = (contentType != null) ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(mediaType)
                .body(inputStreamResource);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteThumbnail(@PathVariable String fileId) {
        thumbnailService.delete(fileId);
        return ResponseEntity.noContent().build();
    }
}

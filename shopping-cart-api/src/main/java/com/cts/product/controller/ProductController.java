package com.cts.product.controller;

import com.cts.common.api.ApiResponse;
import com.cts.common.api.PageResponse;
import com.cts.product.api.CreateProductRequest;
import com.cts.product.api.ProductResponse;
import com.cts.product.service.ProductService;
import com.cts.security.SecurityPrincipal;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // --- PUBLIC ENDPOINTS ---

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.ok("Products fetched successfully",
                productService.getAllActiveProducts(page, size)));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Product fetched successfully",
                productService.getProductById(id)));
    }

    @GetMapping("/products/{id}/thumbnail")
    public ResponseEntity<Resource> getProductThumbnail(@PathVariable Long id) throws Exception {
        GridFsResource resource = productService.getProductThumbnail(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resource.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(new InputStreamResource(resource.getInputStream()));
    }

    // --- SECURED ENDPOINTS ---

    // Note: Only users with the SELLER role can access this endpoint
    @PreAuthorize("hasRole('SELLER')")
    @PostMapping(value = "/seller/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @AuthenticationPrincipal SecurityPrincipal principal,
            @Valid @RequestPart("product") CreateProductRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) {

        Long sellerId = Long.parseLong(principal.userId());
        ProductResponse response = productService.createProduct(sellerId, request, thumbnail);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Product created successfully", response));
    }
}
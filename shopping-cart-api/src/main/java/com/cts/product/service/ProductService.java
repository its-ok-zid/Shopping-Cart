package com.cts.product.service;

import com.cts.common.api.PageResponse;
import com.cts.product.api.CreateProductRequest;
import com.cts.product.api.ProductResponse;
import com.cts.product.api.StockUpdateRequest;
import com.cts.product.api.UpdateProductRequest;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    ProductResponse createProduct(Long sellerId, CreateProductRequest request, MultipartFile thumbnail);
    PageResponse<ProductResponse> getAllActiveProducts(int page, int size);
    ProductResponse getProductById(Long id);
    GridFsResource getProductThumbnail(Long id);

    // Full Seller & Admin lifecycle methods
    ProductResponse updateProduct(Long sellerId, Long productId, UpdateProductRequest request, boolean isAdmin);
    ProductResponse updateStock(Long sellerId, Long productId, StockUpdateRequest request);
    void deactivateProduct(Long sellerId, Long productId, boolean isAdmin);
}
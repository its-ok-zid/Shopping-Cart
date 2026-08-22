package com.cts.product.service;

import com.cts.common.api.PageResponse;
import com.cts.product.api.CreateProductRequest;
import com.cts.product.api.ProductResponse;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    
    ProductResponse createProduct(Long sellerId, CreateProductRequest request, MultipartFile thumbnail);
    
    PageResponse<ProductResponse> getAllActiveProducts(int page, int size);
    
    ProductResponse getProductById(Long id);
    
    GridFsResource getProductThumbnail(Long id);
}
package com.cts.product.service;

import com.cts.common.api.PageResponse;
import com.cts.common.error.ApiException;
import com.cts.product.api.CreateProductRequest;
import com.cts.product.api.ProductResponse;
import com.cts.product.domain.Product;
import com.cts.product.repository.ProductRepository;
import com.cts.user.domain.AppUser;
import com.cts.user.repository.UserRepository;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final GridFsTemplate gridFsTemplate;
    private final GridFsOperations gridFsOperations;

    public ProductServiceImpl(ProductRepository productRepository, 
                              UserRepository userRepository, 
                              GridFsTemplate gridFsTemplate,
                              GridFsOperations gridFsOperations) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.gridFsTemplate = gridFsTemplate;
        this.gridFsOperations = gridFsOperations;
    }

    @Override
    @Transactional
    public ProductResponse createProduct(Long sellerId, CreateProductRequest request, MultipartFile thumbnail) {
        if (productRepository.existsBySkuIgnoreCase(request.sku())) {
            throw ApiException.conflict("SKU_EXISTS", "Product with this SKU already exists");
        }

        AppUser seller = userRepository.findById(sellerId)
                .orElseThrow(() -> ApiException.notFound("Seller profile"));

        String thumbnailId = null;

        // 1. Save Image to MongoDB GridFS
        if (thumbnail != null && !thumbnail.isEmpty()) {
            try {
                ObjectId fileId = gridFsTemplate.store(
                        thumbnail.getInputStream(),
                        thumbnail.getOriginalFilename(),
                        thumbnail.getContentType()
                );
                thumbnailId = fileId.toHexString();
            } catch (Exception e) {
                throw ApiException.badRequest("UPLOAD_FAILED", "Failed to process thumbnail image");
            }
        }

        try {
            // 2. Save Data to PostgreSQL
            Product product = new Product(seller, request.sku(), request.name(), 
                    request.description(), request.price(), request.stock());
            
            if (thumbnailId != null) {
                product.replaceThumbnail(thumbnailId);
            }

            Product savedProduct = productRepository.save(product);
            return mapToResponse(savedProduct);

        } catch (Exception e) {
            // 3. Compensation Pattern: Delete MongoDB image if PostgreSQL fails
            if (thumbnailId != null) {
                gridFsTemplate.delete(new Query(Criteria.where("_id").is(thumbnailId)));
            }
            throw ApiException.badRequest("DB_ERROR", "Failed to save product. Image upload rolled back.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getAllActiveProducts(int page, int size) {
        Page<Product> products = productRepository.findAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );
        return PageResponse.from(products, this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> ApiException.notFound("Product"));
        return mapToResponse(product);
    }

    @Override
    public GridFsResource getProductThumbnail(Long id) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> ApiException.notFound("Product"));

        if (product.getThumbnailId() == null) {
            throw ApiException.notFound("Thumbnail");
        }

        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(product.getThumbnailId())));
        if (file == null) {
            throw ApiException.notFound("Thumbnail file in storage");
        }

        return gridFsOperations.getResource(file);
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSeller().getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getStock() > 0,
                product.isActive(),
                product.getThumbnailId() != null ? "/api/products/" + product.getId() + "/thumbnail" : null,
                product.getUpdatedAt()
        );
    }
}
package com.company.inventoryservice.service.impl;

import com.company.inventoryservice.dto.CreateProductRequest;
import com.company.inventoryservice.dto.ProductResponse;
import com.company.inventoryservice.dto.UpdateProductRequest;
import com.company.inventoryservice.entity.Product;
import com.company.inventoryservice.exception.InsufficientInventoryException;
import com.company.inventoryservice.exception.ProductNotFoundException;
import com.company.inventoryservice.repository.ProductRepository;
import com.company.inventoryservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Transactional
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public void deductInventory(Long id, Integer quantity) {
        log.info("Attempting to deduct inventory for product: {}, quantity: {}", id, quantity);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));

        if (product.getAvailableQuantity() < quantity) {
            throw new InsufficientInventoryException("Insufficient inventory for product: " + id);
        }

        product.setAvailableQuantity(product.getAvailableQuantity() - quantity);
        productRepository.save(product);
        log.info("Inventory deducted successfully for product: {}", id);
    }
    
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
        return new ProductResponse(product);
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setProductName(request.getProductName());
        product.setAvailableQuantity(request.getAvailableQuantity());
        
        Product savedProduct = productRepository.save(product);
        return new ProductResponse(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(UpdateProductRequest request) {
        Product product = productRepository.findById(request.getId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + request.getId()));
        
        if (request.getQuantity() != null) {
            product.setAvailableQuantity(request.getQuantity());
        }
        
        Product savedProduct = productRepository.save(product);
        return new ProductResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public java.util.List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::new)
                .collect(java.util.stream.Collectors.toList());
    }
}

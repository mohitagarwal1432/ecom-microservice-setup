package com.company.inventoryservice.service;

import com.company.inventoryservice.dto.CreateProductRequest;
import com.company.inventoryservice.dto.ProductResponse;
import com.company.inventoryservice.dto.UpdateProductRequest;

import java.util.List;

public interface ProductService {
    void deductInventory(Long id, Integer quantity);
    ProductResponse getProduct(Long id);
    ProductResponse createProduct(CreateProductRequest request);
    ProductResponse updateProduct(UpdateProductRequest request);
    List<ProductResponse> getAllProducts();
}

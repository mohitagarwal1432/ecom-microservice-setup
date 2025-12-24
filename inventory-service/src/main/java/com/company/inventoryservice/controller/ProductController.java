package com.company.inventoryservice.controller;

import com.company.inventoryservice.dto.ApiRequest;
import com.company.inventoryservice.dto.ApiResponse;
import com.company.inventoryservice.dto.CreateProductRequest;
import com.company.inventoryservice.dto.ProductResponse;
import com.company.inventoryservice.dto.UpdateProductRequest;
import com.company.inventoryservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<java.util.List<ProductResponse>> getAllProducts() {
        List<ProductResponse> response = productService.getAllProducts();
        return ApiResponse.success(response, "Products retrieved successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProduct(@PathVariable Long id) {
        ProductResponse response = productService.getProduct(id);
        return ApiResponse.success(response, "Product retrieved successfully");
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ApiRequest<CreateProductRequest> requestWrapper) {
        ProductResponse response = productService.createProduct(requestWrapper.getData());
        return ApiResponse.success(response, "Product created successfully");
    }

    @PutMapping
    public ApiResponse<ProductResponse> updateProduct(@Valid @RequestBody ApiRequest<UpdateProductRequest> requestWrapper) {
        ProductResponse response = productService.updateProduct(requestWrapper.getData());
        return ApiResponse.success(response, "Product updated successfully");
    }
}

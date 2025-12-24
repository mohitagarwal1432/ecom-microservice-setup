package com.company.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String productName;
    private Integer availableQuantity;

    public ProductResponse(com.company.inventoryservice.entity.Product product) {
        this.id = product.getId();
        this.productName = product.getProductName();
        this.availableQuantity = product.getAvailableQuantity();
    }
}

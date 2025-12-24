package com.company.inventoryservice.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CreateProductRequest {
    private String productName;
    
    @Min(value = 0, message = "Available quantity must be at least 0")
    private Integer availableQuantity;
}

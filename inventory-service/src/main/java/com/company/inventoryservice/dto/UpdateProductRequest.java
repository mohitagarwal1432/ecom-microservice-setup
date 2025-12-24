package com.company.inventoryservice.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateProductRequest {
    private Long id;
    
    @Min(value = 0, message = "Quantity must be at least 0")
    private Integer quantity;
}

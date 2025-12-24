package com.company.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderEvent {
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private String eventType; // e.g., ORDER_PLACED, INVENTORY_FAILED
    private String reason;    // Optional, for failures
    private String createdAt;
}

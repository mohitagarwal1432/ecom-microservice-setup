package com.company.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private String status;
    private String cancellationReason;
    private LocalDateTime createdAt;

    public OrderResponse(com.company.orderservice.entity.Order order) {
        this.orderId = order.getId();
        this.productId = order.getProductId();
        this.quantity = order.getQuantity();
        this.status = order.getStatus().name();
        this.cancellationReason = order.getCancellationReason();
        this.createdAt = order.getCreatedAt();
    }
}

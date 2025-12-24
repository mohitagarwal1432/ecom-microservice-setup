package com.company.orderservice.dto;

import com.company.orderservice.enums.OrderStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    @NotNull(message = "Status is required")
    private OrderStatusEnum status;
}

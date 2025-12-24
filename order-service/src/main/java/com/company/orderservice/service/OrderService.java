package com.company.orderservice.service;

import com.company.orderservice.dto.OrderRequest;
import com.company.orderservice.dto.OrderResponse;
import com.company.orderservice.enums.OrderStatusEnum;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    OrderResponse getOrder(Long orderId);
    OrderResponse updateOrderStatus(Long orderId, OrderStatusEnum status);
    void cancelOrder(Long orderId, String reason);
}

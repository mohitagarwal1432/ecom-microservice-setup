package com.company.orderservice.controller;

import com.company.orderservice.dto.ApiRequest;
import com.company.orderservice.dto.ApiResponse;
import com.company.orderservice.dto.OrderRequest;
import com.company.orderservice.dto.OrderResponse;
import com.company.orderservice.dto.UpdateOrderStatusRequest;
import com.company.orderservice.exception.RateLimitExceededException;
import com.company.orderservice.service.OrderService;
import io.github.bucket4j.Bucket;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final Bucket rateLimiter;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody ApiRequest<OrderRequest> requestWrapper) {
        if (rateLimiter.tryConsume(1)) {
            OrderResponse response = orderService.createOrder(requestWrapper.getData());
            return ApiResponse.success(response, "Order placed successfully");
        } else {
            throw new RateLimitExceededException("Too many requests, please try again later");
        }
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrder(@PathVariable Long orderId) {
        OrderResponse response = orderService.getOrder(orderId);
        return ApiResponse.success(response, "Order retrieved successfully");
    }

        @PatchMapping("/{orderId}/status")
    public ApiResponse<OrderResponse> updateOrderStatus(@PathVariable Long orderId, @Valid @RequestBody ApiRequest<UpdateOrderStatusRequest> requestWrapper) {
        OrderResponse response = orderService.updateOrderStatus(orderId, requestWrapper.getData().getStatus());
        return ApiResponse.success(response, "Order status updated successfully");
    }
}

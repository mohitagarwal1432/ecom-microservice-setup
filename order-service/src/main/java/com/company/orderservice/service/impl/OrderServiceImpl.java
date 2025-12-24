package com.company.orderservice.service.impl;

import com.company.orderservice.dto.OrderRequest;
import com.company.orderservice.dto.OrderResponse;
import com.company.orderservice.dto.OrderEvent;
import com.company.orderservice.entity.Order;
import com.company.orderservice.enums.OrderStatusEnum;
import com.company.orderservice.exception.OrderNotFoundException;
import com.company.orderservice.messaging.SqsProducer;
import com.company.orderservice.repository.OrderRepository;
import com.company.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.orderservice.exception.InvalidOrderStateTransitionException;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final SqsProducer sqsProducer;

    private static final Map<OrderStatusEnum, Set<OrderStatusEnum>> ALLOWED_TRANSITIONS = Map.of(
            OrderStatusEnum.PENDING, EnumSet.of(OrderStatusEnum.CONFIRMED, OrderStatusEnum.CANCELLED),
            OrderStatusEnum.CONFIRMED, EnumSet.of(OrderStatusEnum.SHIPPED, OrderStatusEnum.CANCELLED),
            OrderStatusEnum.SHIPPED, EnumSet.of(OrderStatusEnum.DELIVERED),
            OrderStatusEnum.DELIVERED, EnumSet.noneOf(OrderStatusEnum.class),
            OrderStatusEnum.CANCELLED, EnumSet.noneOf(OrderStatusEnum.class)
    );

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        log.info("Creating order for product: {}", request.getProductId());

        Order order = Order.builder()
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .status(OrderStatusEnum.PENDING)
                .build();

        order = orderRepository.save(order);

        OrderEvent event = OrderEvent.builder()
                .orderId(order.getId()) 
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .eventType("ORDER_PLACED")
                .createdAt(order.getCreatedAt().toString())
                .build();

        sqsProducer.publishOrderEvent(event);

        return new OrderResponse(order);
    }

    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
        return new OrderResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatusEnum newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
        
        validateStateTransition(order.getStatus(), newStatus);
        
        order.setStatus(newStatus);
        order = orderRepository.save(order);
        return new OrderResponse(order);
    }

    @Transactional
    public void cancelOrder(Long orderId, String reason) {
        log.info("Cancelling order with ID: {}. Reason: {}", orderId, reason);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
        
        // Ensure cancellation is a valid transition (e.g. not already Delivered)
        validateStateTransition(order.getStatus(), OrderStatusEnum.CANCELLED);
        
        order.setStatus(OrderStatusEnum.CANCELLED);
        order.setCancellationReason(reason);
        orderRepository.save(order);
    }

    private void validateStateTransition(OrderStatusEnum currentStatus, OrderStatusEnum newStatus) {
        Set<OrderStatusEnum> allowedNextStates = ALLOWED_TRANSITIONS.getOrDefault(currentStatus, EnumSet.noneOf(OrderStatusEnum.class));
        if (!allowedNextStates.contains(newStatus)) {
            throw new InvalidOrderStateTransitionException(
                    String.format("Invalid state transition from %s to %s", currentStatus, newStatus));
        }
    }
}

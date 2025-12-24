package com.company.inventoryservice.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.inventoryservice.dto.OrderEvent;
import com.company.inventoryservice.exception.InsufficientInventoryException;
import com.company.inventoryservice.exception.ProductNotFoundException;
import com.company.inventoryservice.service.ProductService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class SqsConsumer {

    private final ProductService productService;
    private final SqsProducer sqsProducer;
    private final ObjectMapper objectMapper;

    @SqsListener("${sqs.queue.order-events}")
    public void listen(String payload) throws JsonProcessingException {
        OrderEvent event = objectMapper.readValue(payload, OrderEvent.class);
        log.info("Received OrderEvent: {}", event);

        if ("ORDER_PLACED".equals(event.getEventType())) {
            try {
                productService.deductInventory(event.getProductId(), event.getQuantity());

                OrderEvent successEvent = OrderEvent.builder()
                        .orderId(event.getOrderId())
                        .productId(event.getProductId())
                        .quantity(event.getQuantity())
                        .eventType("INVENTORY_RESERVED")
                        .createdAt(LocalDateTime.now().toString())
                        .build();

                sqsProducer.publishOrderEvent(successEvent);

            } catch (InsufficientInventoryException | ProductNotFoundException e) {
                log.warn("Insufficient inventory for order: {}. Product: {}. Request: {}", event.getOrderId(), event.getProductId(), event.getQuantity());

                String reason = "";
                if (e instanceof InsufficientInventoryException) {
                    reason = "Insufficient inventory";
                } else {
                    reason = "Product not found";
                }

                OrderEvent failedEvent = OrderEvent.builder()
                        .orderId(event.getOrderId())
                        .productId(event.getProductId())
                        .quantity(event.getQuantity())
                        .eventType("INVENTORY_FAILED")
                        .reason(reason)
                        .createdAt(LocalDateTime.now().toString())
                        .build();

                sqsProducer.publishOrderEvent(failedEvent);
            } catch (Exception e) {
                log.error("Failed to process order event: {}. Reason: {}", event.getOrderId(), e.getMessage());
                throw e;
            }
        }
    }
}

package com.company.orderservice.messaging;

import com.company.orderservice.dto.OrderEvent;
import com.company.orderservice.exception.InvalidOrderStateTransitionException;
import com.company.orderservice.exception.OrderNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.orderservice.enums.OrderStatusEnum;
import com.company.orderservice.service.OrderService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SqsConsumer {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @SqsListener("${sqs.queue.inventory-events}")
    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = 2,
            backoff = @Backoff(delay = 1000)
    )
    public void listen(String payload) throws JsonProcessingException {
        try {
            OrderEvent event = objectMapper.readValue(payload, OrderEvent.class);
            log.info("Received OrderEvent: {}", event);
            if ("INVENTORY_FAILED".equals(event.getEventType())) {
                orderService.cancelOrder(event.getOrderId(), event.getReason());
            } else if ("INVENTORY_RESERVED".equals(event.getEventType())) {
                orderService.updateOrderStatus(event.getOrderId(), OrderStatusEnum.CONFIRMED);
            }
        } catch (InvalidOrderStateTransitionException | OrderNotFoundException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage());
            throw e;
        }
    }
}

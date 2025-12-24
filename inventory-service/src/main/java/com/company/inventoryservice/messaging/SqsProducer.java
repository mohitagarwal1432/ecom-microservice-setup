package com.company.inventoryservice.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.inventoryservice.dto.OrderEvent;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SqsProducer {

    private final SqsTemplate sqsTemplate;
    private final ObjectMapper objectMapper;

    @Value("${sqs.queue.inventory-events}")
    private String inventoryEventsQueue;

    public void publishOrderEvent(OrderEvent event) {
        log.info("Publishing OrderEvent to SQS: {} - Type: {}", event.getOrderId(), event.getEventType());
        try {
            String payload = objectMapper.writeValueAsString(event);
            sqsTemplate.send(to -> to
                    .queue(inventoryEventsQueue)
                    .payload(payload));
        } catch (Exception e) {
            log.error("Failed to publish OrderEvent: {}", e.getMessage());
            throw new RuntimeException("Failed to publish OrderEvent to SQS", e);
        }
    }
}

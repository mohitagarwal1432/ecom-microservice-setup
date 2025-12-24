package com.company.orderservice.messaging;

import com.company.orderservice.dto.OrderEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SqsProducer {

    private final SqsTemplate sqsTemplate;
    private final ObjectMapper objectMapper;

    @Value("${sqs.queue.order-events}")
    private String orderEventsQueue;

    @Retryable(
            retryFor = { RuntimeException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void publishOrderEvent(OrderEvent event) {
        log.info("Publishing OrderEvent to SQS: {}", event.getOrderId());
        try {
            String payload = objectMapper.writeValueAsString(event);
            sqsTemplate.send(to -> to
                    .queue(orderEventsQueue)
                    .payload(payload));
            log.info("Call succeded from SQS");
        } catch (Exception e) {
            log.error("Failed to publish OrderEvent: {}", e.getMessage());
            throw new RuntimeException("Failed to publish order event", e); 
        }
    }
}

package pet.airbooking.io.messaging.impl;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pet.airbooking.core.entity.OutboxEvent;
import pet.airbooking.core.model.EventType;
import pet.airbooking.core.service.KafkaProducerService;
import pet.airbooking.io.messaging.OutboxEventPublisher;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaOutboxEventPublisher implements OutboxEventPublisher {

    private final KafkaProducerService kafkaProducerService;

    @Override
    @Retry(name = "kafkaPublish", fallbackMethod = "fallback")
    public void publish(OutboxEvent event) {

        String topic = resolveTopic(event.getEventType());

        log.info("Publishing outbox event type={} aggregateId={} topic={}",
                event.getEventType(),
                event.getAggregateId(),
                topic);

        kafkaProducerService.send(
                topic,
                String.valueOf(event.getAggregateId()),
                event.getPayload()
        );
    }

    private void fallback(OutboxEvent event, Throwable ex) {
        log.error("FINAL FAIL sending event id={}", event.getId(), ex);
        throw new RuntimeException(ex);
    }

    private String resolveTopic(EventType type) {
        return switch (type) {
            case BOOKING_CREATED -> "booking.created";
            case BOOKING_CONFIRMED -> "booking.confirmed";
            case BOOKING_CANCELLED -> "booking.cancelled";
        };
    }
}

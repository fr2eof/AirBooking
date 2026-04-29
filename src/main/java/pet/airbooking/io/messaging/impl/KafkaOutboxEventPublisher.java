package pet.airbooking.io.messaging.impl;

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
    public void publish(OutboxEvent event) {

        String topic = resolveTopic(event.getEventType());

        log.info("Publishing outbox event type={} aggregateId={} topic={}",
                event.getEventType(),
                event.getAggregateId(),
                topic);

        try {
            kafkaProducerService.send(
                    topic,
                    String.valueOf(event.getAggregateId()),
                    event.getPayload()
            );

            log.info("Outbox event published successfully type={} aggregateId={}",
                    event.getEventType(),
                    event.getAggregateId());

        } catch (Exception ex) {

            log.error("Failed to publish outbox event type={} aggregateId={} topic={}",
                    event.getEventType(),
                    event.getAggregateId(),
                    topic,
                    ex);

            throw ex;
        }
    }

    private String resolveTopic(EventType type) {
        return switch (type) {
            case BOOKING_CREATED -> "booking.created";
            case BOOKING_CONFIRMED -> "booking.confirmed";
            case BOOKING_CANCELLED -> "booking.cancelled";
        };
    }
}

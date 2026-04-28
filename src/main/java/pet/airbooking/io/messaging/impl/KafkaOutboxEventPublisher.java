package pet.airbooking.io.messaging.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pet.airbooking.core.entity.OutboxEvent;
import pet.airbooking.core.model.EventType;
import pet.airbooking.core.service.KafkaProducerService;
import pet.airbooking.io.messaging.OutboxEventPublisher;

@Service
@RequiredArgsConstructor
public class KafkaOutboxEventPublisher implements OutboxEventPublisher {

    private final KafkaProducerService kafkaProducerService;

    @Override
    public void publish(OutboxEvent event) {

        kafkaProducerService.send(
                resolveTopic(event.getEventType()),
                String.valueOf(event.getAggregateId()),
                event.getPayload()
        );
    }

    private String resolveTopic(EventType type) {
        return switch (type) {
            case BOOKING_CREATED -> "booking.created";
            case BOOKING_CONFIRMED -> "booking.confirmed";
            case BOOKING_CANCELLED -> "booking.cancelled";
        };
    }
}

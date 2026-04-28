package pet.airbooking.io.messaging;

import pet.airbooking.core.entity.OutboxEvent;

public interface OutboxEventPublisher {
    void publish(OutboxEvent event);
}

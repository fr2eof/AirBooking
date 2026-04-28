package pet.airbooking.core.service;

import pet.airbooking.core.entity.OutboxEvent;

public interface OutboxService {
    void saveEvent(OutboxEvent event);
}

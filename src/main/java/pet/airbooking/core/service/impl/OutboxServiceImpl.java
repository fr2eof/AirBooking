package pet.airbooking.core.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pet.airbooking.core.entity.OutboxEvent;
import pet.airbooking.core.model.EventType;
import pet.airbooking.core.model.OutboxStatus;
import pet.airbooking.core.repository.OutboxEventRepository;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class OutboxServiceImpl {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void saveEvent(Long aggregateId,
                          EventType eventType,
                          Object payload) {

        try {
            OutboxEvent event = OutboxEvent.builder()
                    .aggregateId(aggregateId)
                    .eventType(eventType)
                    .payload(objectMapper.writeValueAsString(payload))
                    .build();

            repository.save(event);

        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize outbox event", e);
        }
    }

    public long countFailed() {
        return repository.countByStatus(OutboxStatus.FAILED);
    }
}
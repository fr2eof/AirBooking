package pet.airbooking.scheduler.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pet.airbooking.core.entity.OutboxEvent;
import pet.airbooking.core.model.OutboxStatus;
import pet.airbooking.core.repository.OutboxEventRepository;
import pet.airbooking.io.messaging.impl.KafkaOutboxEventPublisher;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxProcessor {

    private final OutboxEventRepository repository;
    private final KafkaOutboxEventPublisher publisher;

    @Scheduled(fixedDelayString = "${outbox.scheduler.delay}")
    @Transactional
    public void process() {

        List<OutboxEvent> events =
                repository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.NEW);

        for (OutboxEvent event : events) {

            try {
                publisher.publish(event);

                event.setStatus(OutboxStatus.SENT);
                event.setProcessedAt(LocalDateTime.now());

            } catch (Exception e) {
                log.error("Failed to publish event {}", event.getId(), e);
            }
        }
    }
}

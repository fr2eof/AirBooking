package pet.airbooking.scheduler.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pet.airbooking.core.model.OutboxStatus;
import pet.airbooking.core.repository.OutboxEventRepository;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxCleanupScheduler {

    private final OutboxEventRepository repository;

    @Scheduled(cron = "0 0 * * * *") // every hour
    public void cleanup() {

        LocalDateTime threshold = LocalDateTime.now().minusDays(1);

        repository.deleteByStatusAndCreatedAtBefore(
                OutboxStatus.SENT,
                threshold
        );

        log.info("Outbox cleanup executed");
    }
}
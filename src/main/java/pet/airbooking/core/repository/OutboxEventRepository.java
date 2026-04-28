package pet.airbooking.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pet.airbooking.core.entity.OutboxEvent;
import pet.airbooking.core.model.OutboxStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus status);

    void deleteByStatusAndCreatedAtBefore(OutboxStatus status, LocalDateTime time);
}

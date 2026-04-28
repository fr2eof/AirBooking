package pet.airbooking.core.repository.IT;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pet.airbooking.core.entity.OutboxEvent;
import pet.airbooking.core.model.EventType;
import pet.airbooking.core.model.OutboxStatus;
import pet.airbooking.core.repository.OutboxEventRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class OutboxEventRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private OutboxEventRepository repository;

    @Nested
    class Save {

        @Test
        void shouldSaveOutboxEvent_withGeneratedId() {
            // Given
            OutboxEvent event = OutboxEvent.builder()
                    .aggregateId(1L)
                    .eventType(EventType.BOOKING_CREATED)
                    .payload("{\"test\":true}")
                    .status(OutboxStatus.NEW)
                    .build();

            // When
            OutboxEvent saved = repository.save(event);

            // Then
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getAggregateId()).isEqualTo(1L);
            assertThat(saved.getEventType()).isEqualTo(EventType.BOOKING_CREATED);
            assertThat(saved.getStatus()).isEqualTo(OutboxStatus.NEW);
        }
    }

    @Nested
    class FindTop100ByStatus {

        @Test
        void shouldReturnEvents_orderedByCreatedAt() {
            // Given
            repository.save(OutboxEvent.builder()
                    .aggregateId(1L)
                    .eventType(EventType.BOOKING_CREATED)
                    .payload("{}")
                    .status(OutboxStatus.NEW)
                    .build());

            repository.save(OutboxEvent.builder()
                    .aggregateId(2L)
                    .eventType(EventType.BOOKING_CONFIRMED)
                    .payload("{}")
                    .status(OutboxStatus.NEW)
                    .build());

            // When
            List<OutboxEvent> events =
                    repository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.NEW);

            // Then
            assertThat(events).hasSize(2);
            assertThat(events)
                    .extracting(OutboxEvent::getStatus)
                    .containsOnly(OutboxStatus.NEW);
        }

        @Test
        void shouldReturnEmpty_whenNoEventsWithStatus() {
            // When
            List<OutboxEvent> events =
                    repository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.SENT);

            // Then
            assertThat(events).isEmpty();
        }
    }

    @Nested
    class DeleteByStatusAndCreatedAtBefore {

        @Test
        void shouldNotDelete_whenStatusDoesNotMatch() {
            // Given
            repository.save(OutboxEvent.builder()
                    .aggregateId(1L)
                    .eventType(EventType.BOOKING_CREATED)
                    .payload("{}")
                    .status(OutboxStatus.NEW)
                    .build());

            LocalDateTime future = LocalDateTime.now().plusDays(1);

            // When
            repository.deleteByStatusAndCreatedAtBefore(
                    OutboxStatus.SENT,
                    future
            );

            // Then
            assertThat(repository.findAll()).hasSize(1);
        }
    }
}

package pet.airbooking.core.service.IT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pet.airbooking.AbstractIntegrationTest;
import pet.airbooking.core.entity.BookingEntity;
import pet.airbooking.core.entity.OutboxEvent;
import pet.airbooking.core.exception.EntityNotFoundException;
import pet.airbooking.core.model.BookingStatus;
import pet.airbooking.core.model.EventType;
import pet.airbooking.core.repository.BookingJpaRepository;
import pet.airbooking.core.repository.OutboxEventRepository;
import pet.airbooking.core.service.BookingService;
import pet.airbooking.io.dto.BookingEntityDTO;
import pet.airbooking.io.dto.response.CreateBookingResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookingServiceImplIntegrationTest extends AbstractIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    private BookingService service;

    @Autowired
    private BookingJpaRepository repository;
    @Autowired
    private OutboxEventRepository outboxRepository;

    @BeforeEach
    void clean() {
        outboxRepository.deleteAll();
        repository.deleteAll();
    }

    @Nested
    class Create {

        @Test
        void shouldCreateBooking_andPersistOutboxEvent() {

            Long userId = 1L;
            Long listingId = 2L;

            CreateBookingResponse response =
                    service.create(userId, listingId);

            assertThat(response.getBookingId()).isNotNull();

            BookingEntity entity =
                    repository.findById(response.getBookingId()).orElseThrow();

            assertThat(entity.getStatus()).isEqualTo(BookingStatus.PENDING);

            OutboxEvent event =
                    outboxRepository.findAll().get(0);

            assertThat(event.getEventType())
                    .isEqualTo(EventType.BOOKING_CREATED);

            assertThat(event.getAggregateId())
                    .isEqualTo(response.getBookingId());
        }
    }

    @Nested
    class Get {

        @Test
        void shouldReturnBookingDto() {
            // Given
            BookingEntity entity = new BookingEntity(1L, 2L);
            entity.setStatus(BookingStatus.PENDING);

            BookingEntity saved = repository.save(entity);

            // When
            BookingEntityDTO actual =
                    service.get(saved.getId());

            // Then
            assertThat(actual).isNotNull();
            assertThat(actual.getId()).isEqualTo(saved.getId());
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldDeleteBooking_andWriteOutboxEvent() {

            BookingEntity saved =
                    repository.save(new BookingEntity(1L, 2L));

            Long id = saved.getId();

            service.delete(id);

            assertThat(repository.findById(id)).isEmpty();

            OutboxEvent event =
                    outboxRepository.findAll().get(0);

            assertThat(event.getEventType())
                    .isEqualTo(EventType.BOOKING_CANCELLED);
        }

        @Test
        void shouldThrowException_whenBookingNotFound() {
            // Given
            Long id = 999L;

            // When / Then
            assertThatThrownBy(() -> service.delete(id))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("not found");
        }
    }

    @Nested
    class Confirm {

        @Test
        void shouldConfirmBooking_andWriteOutboxEvent() {

            BookingEntity saved =
                    repository.save(new BookingEntity(1L, 2L));

            service.confirm(saved.getId());

            BookingEntity updated =
                    repository.findById(saved.getId()).orElseThrow();

            assertThat(updated.getStatus())
                    .isEqualTo(BookingStatus.CONFIRMED);

            OutboxEvent event =
                    outboxRepository.findAll().get(0);

            assertThat(event.getEventType())
                    .isEqualTo(EventType.BOOKING_CONFIRMED);
        }
    }

    @Nested
    class Cancel {

        @Test
        void shouldCancelBooking_andWriteOutboxEvent() {

            BookingEntity saved =
                    repository.save(new BookingEntity(1L, 2L));

            service.cancel(saved.getId());

            BookingEntity updated =
                    repository.findById(saved.getId()).orElseThrow();

            assertThat(updated.getStatus())
                    .isEqualTo(BookingStatus.CANCELLED);

            OutboxEvent event =
                    outboxRepository.findAll().get(0);

            assertThat(event.getEventType())
                    .isEqualTo(EventType.BOOKING_CANCELLED);
        }
    }
}

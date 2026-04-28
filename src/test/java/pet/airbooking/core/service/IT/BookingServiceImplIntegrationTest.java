package pet.airbooking.core.service.IT;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pet.airbooking.core.entity.BookingEntity;
import pet.airbooking.core.exception.EntityNotFoundException;
import pet.airbooking.core.model.BookingStatus;
import pet.airbooking.core.repository.BookingJpaRepository;
import pet.airbooking.core.service.BookingService;
import pet.airbooking.io.dto.BookingEntityDTO;
import pet.airbooking.io.dto.response.CreateBookingResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookingServiceImplIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    private BookingService service;

    @Autowired
    private BookingJpaRepository repository;
    @Nested
    class Create {

        @Test
        void shouldCreateBooking_andPersistToDatabase() {
            // Given
            Long userId = 1L;
            Long listingId = 2L;

            // When
            CreateBookingResponse actualResponse =
                    service.create(userId, listingId);

            // Then
            assertThat(actualResponse.getBookingId()).isNotNull();

            BookingEntity actualEntity =
                    repository.findById(actualResponse.getBookingId()).orElseThrow();

            assertThat(actualEntity.getUserId()).isEqualTo(1L);
            assertThat(actualEntity.getListingId()).isEqualTo(2L);
            assertThat(actualEntity.getStatus()).isEqualTo(BookingStatus.PENDING);
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
        void shouldDeleteBooking_fromDatabase() {
            // Given
            BookingEntity saved =
                    repository.save(new BookingEntity(1L, 2L));

            Long id = saved.getId();

            // When
            service.delete(id);

            // Then
            assertThat(repository.findById(id)).isEmpty();
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
        void shouldConfirmBooking_andUpdateStatus() {
            // Given
            BookingEntity saved =
                    repository.save(new BookingEntity(1L, 2L));

            Long id = saved.getId();

            // When
            BookingEntityDTO actual =
                    service.confirm(id);

            // Then
            BookingEntity updated =
                    repository.findById(id).orElseThrow();

            assertThat(updated.getStatus())
                    .isEqualTo(BookingStatus.CONFIRMED);

            assertThat(actual.getId()).isEqualTo(id);
        }
    }
    @Nested
    class Cancel {

        @Test
        void shouldCancelBooking_andUpdateStatus() {
            // Given
            BookingEntity saved =
                    repository.save(new BookingEntity(1L, 2L));

            Long id = saved.getId();

            // When
            BookingEntityDTO actual =
                    service.cancel(id);

            // Then
            BookingEntity updated =
                    repository.findById(id).orElseThrow();

            assertThat(updated.getStatus())
                    .isEqualTo(BookingStatus.CANCELLED);

            assertThat(actual.getId()).isEqualTo(id);
        }
    }
}

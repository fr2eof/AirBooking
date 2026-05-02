package pet.airbooking.core.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pet.airbooking.core.entity.BookingEntity;
import pet.airbooking.core.exception.EntityNotFoundException;
import pet.airbooking.core.model.BookingStatus;
import pet.airbooking.core.model.EventType;
import pet.airbooking.core.repository.BookingJpaRepository;
import pet.airbooking.core.service.impl.BookingServiceImpl;
import pet.airbooking.core.service.impl.OutboxServiceImpl;
import pet.airbooking.io.dto.BookingEntityDTO;
import pet.airbooking.io.dto.response.CreateBookingResponse;
import pet.airbooking.io.mapper.BookingMapper;
import pet.airbooking.io.metrics.BookingMetrics;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingJpaRepository repository;

    @Mock
    private BookingMapper mapper;

    @Mock
    private OutboxServiceImpl outboxService;

    @Mock
    private BookingMetrics bookingMetrics;

    @InjectMocks
    private BookingServiceImpl service;

    @Nested
    class Create {

        @Test
        void shouldCreateBooking_withPendingStatus() {
            // Given
            Long userId = 1L;
            BigDecimal amount = BigDecimal.valueOf(300);

            BookingEntity savedEntity = new BookingEntity(userId, amount);
            savedEntity.setId(10L);

            given(repository.save(any())).willReturn(savedEntity);

            // When
            CreateBookingResponse actualResponse =
                    service.create(userId, amount);

            // Then
            assertThat(actualResponse.getBookingId()).isEqualTo(10L);

            verify(repository).save(argThat(entity ->
                    entity.getUserId().equals(userId) &&
                            entity.getAmount().equals(amount) &&
                            entity.getStatus() == BookingStatus.PENDING
            ));

            verify(outboxService).saveEvent(
                    anyLong(),
                    eq(EventType.BOOKING_CREATED),
                    any()
            );
        }
        @Test
        void shouldReturnIdFromSavedEntity() {
            // Given
            Long userId = 1L;
            BigDecimal amount =  BigDecimal.valueOf(300);

            BookingEntity savedEntity = new BookingEntity(userId, amount);
            savedEntity.setId(99L);

            given(repository.save(any())).willReturn(savedEntity);

            // When
            CreateBookingResponse actualResponse =
                    service.create(userId, amount);

            // Then
            assertThat(actualResponse.getBookingId()).isEqualTo(99L);

            verify(outboxService).saveEvent(
                    anyLong(),
                    eq(EventType.BOOKING_CREATED),
                    any()
            );
        }
    }

    @Nested
    class Get {

        @Test
        void shouldReturnDto_whenBookingExists() {
            // Given
            Long id = 1L;

            BookingEntity entity = new BookingEntity(1L, BigDecimal.valueOf(300));

            BookingEntityDTO expectedDto = new BookingEntityDTO();

            given(repository.findById(id)).willReturn(Optional.of(entity));
            given(mapper.toDto(entity)).willReturn(expectedDto);

            // When
            BookingEntityDTO actualDto = service.get(id);

            // Then
            assertThat(actualDto).isEqualTo(expectedDto);
        }

        @Test
        void shouldThrowException_whenBookingNotFound() {
            // Given
            Long id = 1L;

            given(repository.findById(id)).willReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> service.get(id))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Booking with id = 1 not found");
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldDelete_whenExists() {
            // Given
            Long id = 1L;
            given(repository.existsById(id)).willReturn(true);

            // When
            service.delete(id);

            // Then
            verify(repository).deleteById(id);

            verify(outboxService).saveEvent(
                    eq(id),
                    eq(EventType.BOOKING_CANCELLED),
                    any()
            );
        }

        @Test
        void shouldThrow_whenNotExists() {
            // Given
            Long id = 1L;
            given(repository.existsById(id)).willReturn(false);

            // When / Then
            assertThatThrownBy(() -> service.delete(id))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Booking with id = 1 not found");
        }
    }

    @Nested
    class Confirm {

        @Test
        void shouldConfirmBooking() {
            // Given
            Long id = 1L;

            BookingEntity entity = spy(new BookingEntity(1L, BigDecimal.valueOf(300)));

            BookingEntity savedEntity = new BookingEntity(1L, BigDecimal.valueOf(300));
            savedEntity.setId(id);

            BookingEntityDTO expectedDto = new BookingEntityDTO();

            given(repository.findById(id)).willReturn(Optional.of(entity));
            given(repository.save(entity)).willReturn(savedEntity);
            given(mapper.toDto(savedEntity)).willReturn(expectedDto);

            // When
            BookingEntityDTO actualDto = service.confirm(id);

            // Then
            assertThat(actualDto).isEqualTo(expectedDto);

            verify(entity).confirm();
            verify(repository).save(entity);

            verify(outboxService).saveEvent(
                    eq(id),
                    eq(EventType.BOOKING_CONFIRMED),
                    any()
            );
        }

        @Test
        void shouldThrowException_whenBookingNotFound() {
            // Given
            Long id = 1L;

            given(repository.findById(id)).willReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> service.confirm(id))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    class Cancel {

        @Test
        void shouldCancelBooking() {
            // Given
            Long id = 1L;

            BookingEntity entity = spy(new BookingEntity(1L, BigDecimal.valueOf(300)));

            BookingEntity savedEntity = new BookingEntity(1L, BigDecimal.valueOf(300));
            savedEntity.setId(id);

            BookingEntityDTO expectedDto = new BookingEntityDTO();

            given(repository.findById(id)).willReturn(Optional.of(entity));
            given(repository.save(entity)).willReturn(savedEntity);
            given(mapper.toDto(savedEntity)).willReturn(expectedDto);

            // When
            BookingEntityDTO actualDto = service.cancel(id);

            // Then
            assertThat(actualDto).isEqualTo(expectedDto);

            verify(entity).cancel();
            verify(repository).save(entity);

            verify(outboxService).saveEvent(
                    eq(id),
                    eq(EventType.BOOKING_CANCELLED),
                    any()
            );
        }

        @Test
        void shouldThrowException_whenBookingNotFound() {
            // Given
            Long id = 1L;

            given(repository.findById(id)).willReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> service.cancel(id))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }
}

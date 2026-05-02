package pet.airbooking.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pet.airbooking.core.entity.BookingEntity;
import pet.airbooking.core.exception.EntityNotFoundException;
import pet.airbooking.core.model.BookingStatus;
import pet.airbooking.core.model.EventType;
import pet.airbooking.core.repository.BookingJpaRepository;
import pet.airbooking.core.service.BookingService;
import pet.airbooking.io.dto.BookingEntityDTO;
import pet.airbooking.io.dto.event.BookingCancelledEvent;
import pet.airbooking.io.dto.event.BookingConfirmedEvent;
import pet.airbooking.io.dto.event.BookingCreatedEvent;
import pet.airbooking.io.dto.response.CreateBookingResponse;
import pet.airbooking.io.mapper.BookingMapper;
import pet.airbooking.io.metrics.BookingMetrics;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingJpaRepository repository;
    private final BookingMapper mapper;
    private final OutboxServiceImpl outboxService;
    private final BookingMetrics metrics;


    @Override
    @Transactional
    public CreateBookingResponse create(Long userId, BigDecimal amount) {
        BookingEntity entity = new BookingEntity(userId, amount);
        entity.setStatus(BookingStatus.PENDING);
        BookingEntity saved = repository.save(entity);

        outboxService.saveEvent(
                saved.getId(),
                EventType.BOOKING_CREATED,
                new BookingCreatedEvent(
                        saved.getId(),
                        saved.getUserId(),
                        saved.getAmount() //todo replace with rest request announcement microservice
                )
        );
        metrics.incrementCreated();
        return new CreateBookingResponse(saved.getId());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "bookings", key = "#id")
    public BookingEntityDTO get(Long id) {
        BookingEntity entity = getBookingById(id);
        return mapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingEntityDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(en -> mapper.toDto(en));
    }

    @Override
    @Transactional
    @CacheEvict(value = "bookings", key = "#id")
    public BookingEntityDTO confirm(Long id) {
        BookingEntity entity = getBookingById(id);
        entity.confirm();
        BookingEntity saved = repository.save(entity);

        outboxService.saveEvent(
                saved.getId(),
                EventType.BOOKING_CONFIRMED,
                new BookingConfirmedEvent(saved.getId())
        );
        metrics.incrementConfirmed();

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "bookings", key = "#id")
    public BookingEntityDTO cancel(Long id) {
        BookingEntity entity = getBookingById(id);
        entity.cancel();
        BookingEntity saved = repository.save(entity);

        outboxService.saveEvent(
                saved.getId(),
                EventType.BOOKING_CANCELLED,
                new BookingCancelledEvent(saved.getId())
        );
        metrics.incrementCancelled();

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "bookings", key = "#id")
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("Booking with id = %d not found", id)
            );
        }
        repository.deleteById(id);

        outboxService.saveEvent(
                id,
                EventType.BOOKING_CANCELLED,
                new BookingCancelledEvent(id)
        );
    }

    private BookingEntity getBookingById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Booking with id = %d not found", id)));
    }
}

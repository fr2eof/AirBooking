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


@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingJpaRepository repository;
    private final BookingMapper mapper;
    private final OutboxServiceImpl outboxService;

    @Override
    @Transactional
    public CreateBookingResponse create(Long userId, Long listingId) {
        BookingEntity entity = new BookingEntity(userId, listingId);
        entity.setStatus(BookingStatus.PENDING);
        BookingEntity saved = repository.save(entity);

        outboxService.saveEvent(
                saved.getId(),
                EventType.BOOKING_CREATED,
                new BookingCreatedEvent(
                        saved.getId(),
                        saved.getUserId(),
                        saved.getListingId()
                )
        );
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

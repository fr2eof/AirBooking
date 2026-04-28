package pet.airbooking.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pet.airbooking.core.entity.BookingEntity;
import pet.airbooking.core.exception.EntityNotFoundException;
import pet.airbooking.core.model.BookingStatus;
import pet.airbooking.core.repository.BookingJpaRepository;
import pet.airbooking.core.service.BookingService;
import pet.airbooking.io.dto.BookingEntityDTO;
import pet.airbooking.io.dto.response.CreateBookingResponse;
import pet.airbooking.io.mapper.BookingMapper;


@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingJpaRepository repository;
    private final BookingMapper mapper;

    @Override
    @Transactional
    public CreateBookingResponse create(Long userId, Long listingId) {
        BookingEntity entity = new BookingEntity(userId, listingId);
        entity.setStatus(BookingStatus.PENDING);

        return new CreateBookingResponse(repository.save(entity).getId());
    }

    @Override
    @Transactional(readOnly = true)
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
    public BookingEntityDTO confirm(Long id) {
        BookingEntity entity = getBookingById(id);
        entity.confirm();
        return mapper.toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public BookingEntityDTO cancel(Long id) {
        BookingEntity entity = getBookingById(id);
        entity.cancel();
        return mapper.toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("Booking with id = %d not found", id)
            );
        }
        repository.deleteById(id);
    }

    private BookingEntity getBookingById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Booking with id = %d not found", id)));
    }
}

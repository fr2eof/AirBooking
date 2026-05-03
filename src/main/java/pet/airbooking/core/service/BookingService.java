package pet.airbooking.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pet.airbooking.io.dto.BookingEntityDTO;
import pet.airbooking.io.dto.response.CreateBookingResponse;

import java.math.BigDecimal;

public interface BookingService {
    CreateBookingResponse create(Long userId, BigDecimal amount);
    BookingEntityDTO get(Long id);
    Page<BookingEntityDTO> getAll(Pageable pageable);
    BookingEntityDTO confirm(Long id);
    BookingEntityDTO cancel(Long id);
     void delete(Long id);
}

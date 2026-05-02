package pet.airbooking.io.mapper;

import org.springframework.stereotype.Component;
import pet.airbooking.core.entity.BookingEntity;
import pet.airbooking.io.dto.BookingEntityDTO;

@Component
public class BookingMapper {
    public BookingEntityDTO toDto(BookingEntity entity) {
        return new BookingEntityDTO(
                entity.getId(),
                entity.getUserId(),
                entity.getAmount(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public BookingEntity toEntity(BookingEntityDTO dto) {
        return new BookingEntity(dto.getUserId(), dto.getAmount(), dto.getStatus());
    }
}
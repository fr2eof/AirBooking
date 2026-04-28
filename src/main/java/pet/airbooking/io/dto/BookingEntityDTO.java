package pet.airbooking.io.dto;


import lombok.Getter;
import lombok.Setter;
import pet.airbooking.core.model.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingEntityDTO {

    private Long id;
    private Long userId;
    private Long listingId;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BookingEntityDTO() {
    }

    public BookingEntityDTO(Long id, Long userId, Long listingId,
                            BookingStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.listingId = listingId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}

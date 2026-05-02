package pet.airbooking.io.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pet.airbooking.core.model.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class BookingEntityDTO {

    private Long id;
    private Long userId;
    private BigDecimal amount;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BookingEntityDTO() {
    }

    public BookingEntityDTO(Long id, Long userId, BigDecimal amount,
                            BookingStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}

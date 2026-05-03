package pet.airbooking.core.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pet.airbooking.core.exception.IllegalBookingStatusException;
import pet.airbooking.core.model.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Version
    private long version;

    public BookingEntity(Long userId, BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
        this.status = BookingStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public BookingEntity(Long userId, BigDecimal amount, BookingStatus status) {
        this.userId = userId;
        this.amount = amount;
        this.status = status;
    }

    public void confirm() {
        if (getStatus() != BookingStatus.PENDING) {
            throw new IllegalBookingStatusException("Cannot confirm booking");
        } else {
            setStatus(BookingStatus.CONFIRMED);
        }
    }

    public void cancel() {
        if (getStatus() != BookingStatus.PENDING) {
            throw new IllegalBookingStatusException("Cannot cancel booking");
        } else {
            setStatus(BookingStatus.CANCELLED);
        }
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}

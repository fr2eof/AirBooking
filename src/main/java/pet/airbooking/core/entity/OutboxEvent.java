package pet.airbooking.core.entity;

import jakarta.persistence.*;
import lombok.*;
import pet.airbooking.core.model.EventType;
import pet.airbooking.core.model.OutboxStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long aggregateId;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private LocalDateTime createdAt;

    private LocalDateTime processedAt;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = OutboxStatus.NEW;
    }
}

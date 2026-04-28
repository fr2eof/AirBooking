package pet.airbooking.io.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BookingCreatedEvent {
    private Long bookingId;
    private Long userId;
    private Long listingId;
}

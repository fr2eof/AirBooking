package pet.airbooking.io.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BookingCancelledEvent {
    private Long bookingId;
}

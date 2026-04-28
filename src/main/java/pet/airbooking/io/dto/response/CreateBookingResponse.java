package pet.airbooking.io.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBookingResponse {
    private Long bookingId;

    public CreateBookingResponse(Long id) {
        this.bookingId = id;
    }
}

package pet.airbooking.io.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBookingRequest {
    private Long userId;
    private Long listingId;
}

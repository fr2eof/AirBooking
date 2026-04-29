package pet.airbooking.io.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateBookingRequest {
    private Long userId;
    private Long listingId;
}

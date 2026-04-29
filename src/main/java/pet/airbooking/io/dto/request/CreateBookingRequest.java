package pet.airbooking.io.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateBookingRequest {

    @NotNull(message = "userId must not be null")
    @PositiveOrZero
    private Long userId;

    @NotNull(message = "listingId must not be null")
    @PositiveOrZero
    private Long listingId;
}

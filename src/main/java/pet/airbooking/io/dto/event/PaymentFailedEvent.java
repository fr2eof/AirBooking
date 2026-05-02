package pet.airbooking.io.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PaymentFailedEvent {
    private Long id;
    private Long bookingId;
    private String reason;

}

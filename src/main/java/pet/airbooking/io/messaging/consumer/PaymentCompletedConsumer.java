package pet.airbooking.io.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pet.airbooking.core.service.BookingService;
import pet.airbooking.io.dto.event.PaymentCompletedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentCompletedConsumer {

    private final ObjectMapper objectMapper;
    private final BookingService bookingService;

    @KafkaListener(
            topics = "payment.completed",
            groupId = "airbooking-payment"
    )
    public void handle(String message) {
        log.info("Received payment.completed: {}", message);

        try {
            PaymentCompletedEvent event =
                    objectMapper.readValue(message, PaymentCompletedEvent.class);

            bookingService.confirm(event.getBookingId());

        } catch (Exception e) {
            log.error("Failed to process payment.completed: {}", message, e);
        }
    }
}
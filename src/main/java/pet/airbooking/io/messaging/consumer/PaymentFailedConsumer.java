package pet.airbooking.io.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pet.airbooking.core.service.BookingService;
import pet.airbooking.io.dto.event.PaymentFailedEvent;
import pet.airbooking.io.messaging.producer.DlqProducer;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentFailedConsumer {

    private final ObjectMapper objectMapper;
    private final BookingService bookingService;
    private final DlqProducer dlqProducer;

    @KafkaListener(
            topics = "payment.failed",
            groupId = "airbooking-payment"
    )
    public void handle(String message) {
        log.info("Received payment.failed: {}", message);

        try {
            PaymentFailedEvent event =
                    objectMapper.readValue(message, PaymentFailedEvent.class);

            bookingService.cancel(event.getBookingId());

        } catch (Exception e) {
            log.error("Failed to process payment.failed: {}", message, e);
            dlqProducer.send("payment.failed.dlq", message);
        }
    }
}
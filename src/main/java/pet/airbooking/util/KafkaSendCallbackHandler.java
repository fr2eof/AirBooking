package pet.airbooking.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class KafkaSendCallbackHandler {
    public BiConsumer<SendResult<String, String>, Throwable> handle(String topic, String key, String message) {
        return (result, ex) -> {
            if (ex == null) {
                log.info("Kafka SENT topic={} key={} offset={} message={}", topic, key, result.getRecordMetadata().offset(), message);
            } else {
                log.error("Kafka FAILED topic={} key={} message={}", topic, key, message, ex);
            }
        };
    }
}
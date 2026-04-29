package pet.airbooking.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pet.airbooking.core.service.KafkaProducerService;
import pet.airbooking.util.KafkaSendCallbackHandler;

@Service
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaSendCallbackHandler callbackHandler;

    @Override
    public void send(String topic, String key, String message) {
        kafkaTemplate.send(topic, key, message)
                .whenComplete(callbackHandler.handle(topic, key, message));
    }
}

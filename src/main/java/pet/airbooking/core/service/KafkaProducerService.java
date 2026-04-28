package pet.airbooking.core.service;

public interface KafkaProducerService {
    void send(String topic, String key, String message);
}

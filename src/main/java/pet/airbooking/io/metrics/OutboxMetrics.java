package pet.airbooking.io.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import pet.airbooking.core.service.impl.OutboxServiceImpl;

@Component
public class OutboxMetrics {
    public OutboxMetrics(MeterRegistry registry, OutboxServiceImpl outboxService) {
        Gauge.builder("outbox_failed_events", outboxService, OutboxServiceImpl::countFailed)
                .description("Number of unsent outbox events")
                .register(registry);
    }
}

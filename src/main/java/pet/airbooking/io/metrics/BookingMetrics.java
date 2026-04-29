package pet.airbooking.io.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class BookingMetrics {

    private final Counter created;
    private final Counter confirmed;
    private final Counter cancelled;

    public BookingMetrics(MeterRegistry registry) {

        this.created = Counter.builder("bookings_total")
                .tag("status", "created")
                .register(registry);

        this.confirmed = Counter.builder("bookings_total")
                .tag("status", "confirmed")
                .register(registry);

        this.cancelled = Counter.builder("bookings_total")
                .tag("status", "cancelled")
                .register(registry);
    }

    public void incrementCreated() {
        created.increment();
    }

    public void incrementConfirmed() {
        confirmed.increment();
    }

    public void incrementCancelled() {
        cancelled.increment();
    }
}

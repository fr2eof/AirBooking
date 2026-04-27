package pet.airbooking.core.exception;

public class IllegalBookingStatusException extends RuntimeException {
    public IllegalBookingStatusException(String message) {
        super(message);
    }
}

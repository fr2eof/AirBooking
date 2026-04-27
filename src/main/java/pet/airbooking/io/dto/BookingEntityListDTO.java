package pet.airbooking.io.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookingEntityListDTO {
    private List<BookingEntityDTO> bookings;

    public BookingEntityListDTO(List<BookingEntityDTO> all) {
        this.bookings = all;
    }
}

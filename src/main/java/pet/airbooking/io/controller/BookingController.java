package pet.airbooking.io.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pet.airbooking.core.service.BookingService;
import pet.airbooking.io.dto.BookingEntityDTO;
import pet.airbooking.io.dto.request.CreateBookingRequest;
import pet.airbooking.io.dto.response.CreateBookingResponse;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    @PostMapping
    public CreateBookingResponse create(@RequestBody CreateBookingRequest request) {
        return service.create(request.getUserId(), request.getListingId());
    }

    @GetMapping("/{id}")
    public BookingEntityDTO get(@PathVariable Long id) {
        return service.get(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping
    public Page<BookingEntityDTO> getAll(Pageable pageable) {
        return service.getAll(pageable);
    }

    @PostMapping("/{id}/confirm")
    public BookingEntityDTO confirm(@PathVariable Long id) {
        return service.confirm(id);
    }

    @PostMapping("/{id}/cancel")
    public BookingEntityDTO cancel(@PathVariable Long id) {
        return service.cancel(id);
    }

}

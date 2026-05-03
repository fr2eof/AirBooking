package pet.airbooking.io.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
    public CreateBookingResponse create(@Valid @RequestBody CreateBookingRequest request) {
        return service.create(request.getUserId(), request.getAmount());
    }

    @GetMapping("/{id}")
    public BookingEntityDTO get(@PathVariable @NotNull @PositiveOrZero Long id) {
        return service.get(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @NotNull @PositiveOrZero Long id) {
        service.delete(id);
    }

    @GetMapping
    public Page<BookingEntityDTO> getAll(Pageable pageable) {
        return service.getAll(pageable);
    }

    @PostMapping("/{id}/confirm")
    public BookingEntityDTO confirm(@PathVariable @NotNull @PositiveOrZero Long id) {
        return service.confirm(id);
    }

    @PostMapping("/{id}/cancel")
    public BookingEntityDTO cancel(@PathVariable @NotNull @PositiveOrZero Long id) {
        return service.cancel(id);
    }

}

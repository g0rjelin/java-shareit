package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable @Min(1) Long bookingId) {
        return bookingService.findBookingById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> findBookingsByState(@RequestHeader("X-Sharer-User-Id") Long bookerId, @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.findBookingsByState(bookerId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> findBookingsOwnerByState(@RequestHeader("X-Sharer-User-Id") Long ownerId, @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.findBookingsOwnerByState(ownerId, state);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long bookerId, @Valid @RequestBody BookingShortDto newBookingShortDto) {
        return bookingService.create(bookerId, newBookingShortDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable @Min(1) Long bookingId, @RequestParam Boolean approved) {
        return bookingService.update(ownerId, bookingId, approved);
    }

}

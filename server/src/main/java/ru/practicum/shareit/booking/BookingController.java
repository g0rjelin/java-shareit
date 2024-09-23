package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
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

import static ru.practicum.shareit.common.CommonConstants.X_SHARER_USER_ID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@RequestHeader(X_SHARER_USER_ID) Long userId, @PathVariable Long bookingId) {
        return bookingService.findBookingById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> findBookingsByState(@RequestHeader(X_SHARER_USER_ID) Long bookerId,
                                                      @RequestParam(defaultValue = "ALL") String state,
                                                      @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @RequestParam(name = "size", defaultValue = "10") Integer size) {
        /*BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new BadRequestParamException("Unknown state: " + state));*/
        return bookingService.findBookingsByState(bookerId, BookingState.valueOf(state), from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> findBookingsOwnerByState(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                                                           @RequestParam(defaultValue = "ALL") String state,
                                                           @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                           @RequestParam(name = "size", defaultValue = "10") Integer size) {
        /*BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new BadRequestParamException("Unknown state: " + state));*/
        return bookingService.findBookingsOwnerByState(ownerId, BookingState.valueOf(state), from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@RequestHeader(X_SHARER_USER_ID) Long bookerId, @RequestBody BookingShortDto newBookingShortDto) {
        return bookingService.create(bookerId, newBookingShortDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader(X_SHARER_USER_ID) Long ownerId, @PathVariable Long bookingId, @RequestParam Boolean approved) {
        return bookingService.update(ownerId, bookingId, approved);
    }

}

package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

public interface BookingService {

    BookingDto findBookingById(Long userId, Long bookingId);

    Collection<BookingDto> findBookingsByState(Long bookerId, BookingState state);

    Collection<BookingDto> findBookingsOwnerByState(Long ownerId, BookingState state);

    BookingDto create(Long bookerId, BookingShortDto newBookingShortDto);

    BookingDto update(Long ownerId, Long bookingId, Boolean approved);
}

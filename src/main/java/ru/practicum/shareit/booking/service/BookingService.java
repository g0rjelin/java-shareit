package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

public interface BookingService {

    Booking getBookingById(Long bookingId);

    BookingDto findBookingById(Long userId, Long bookingId);

    Collection<BookingDto> findBookingsByState(Long bookerId, String state);

    Collection<BookingDto> findBookingsOwnerByState(Long ownerId, String state);

    BookingDto create(Long bookerId, BookingShortDto newBookingShortDto);

    BookingDto update(Long ownerId, Long bookingId, Boolean approved);
}

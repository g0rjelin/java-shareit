package ru.practicum.shareit.booking.validation;

import jakarta.validation.ValidationException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.WrongDateIntervalException;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class BookingValidation {
    static final String NOT_VALID_INTERVAL_ERROR = "Указан некорректный интервал бронирования";

    public static void validateBookingDate(BookingShortDto bookingShortDto) throws ValidationException {
        if (!bookingShortDto.getStart().isBefore(bookingShortDto.getEnd())) {
            throw new WrongDateIntervalException(NOT_VALID_INTERVAL_ERROR);
        }
    }

}

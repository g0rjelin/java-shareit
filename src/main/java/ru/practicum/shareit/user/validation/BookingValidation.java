package ru.practicum.shareit.user.validation;

import jakarta.validation.ValidationException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.BookingState;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class BookingValidation {
    static final String NOT_VALID_STATE_ERROR = "Указано некорректное значение параметра запроса state";
    static final String NOT_VALID_INTERVAL_ERROR = "Указан некорректный интервал бронирования";

    public static void validateBookingState(String bookingState) throws ValidationException {
        try {
            BookingState state = BookingState.valueOf(bookingState);
        } catch (IllegalArgumentException e) {
            throwValidationException(NOT_VALID_STATE_ERROR);
        }
    }

    public static void validateBookingDate(BookingShortDto bookingShortDto) throws ValidationException {
        if (!bookingShortDto.getStart().isBefore(bookingShortDto.getEnd())) {
            throw new ValidationException(NOT_VALID_INTERVAL_ERROR);
        }
    }

    private static void throwValidationException(String message) {
        log.error(message);
        throw new ValidationException(message);
    }

}

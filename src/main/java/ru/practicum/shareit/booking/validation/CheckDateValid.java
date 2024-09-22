package ru.practicum.shareit.booking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookingShortDto;

public class CheckDateValid implements ConstraintValidator<StartBeforeEndDateValid, BookingShortDto> {
    @Override
    public void initialize(StartBeforeEndDateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingShortDto bookingShortDto, ConstraintValidatorContext constraintValidatorContext) {
        return bookingShortDto.getStart().isBefore(bookingShortDto.getEnd());
    }
}
package ru.practicum.shareit.user.validation;

import jakarta.validation.ValidationException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.dto.UserDto;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@UtilityClass
public class UserValidator {
    static final String EMAIL_FORMAT_ERROR = "Электронная почта должна содержать символ @";

    public static void validateFormat(UserDto userDto) {
        if (userDto.getEmail() != null && !userDto.getEmail().contains("@")) {
            throwValidationException(EMAIL_FORMAT_ERROR);
        }
    }

    private static void throwValidationException(String message) {
        log.error(message);
        throw new ValidationException(message);
    }
}

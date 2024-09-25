package ru.practicum.shareit.item.validation;

import jakarta.validation.ValidationException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
@UtilityClass
public class ItemValidation {
    static final String NULL_NAME_ERROR = "Название вещи не может быть пустым или состоять только из пробелов";
    static final String NULL_DESCRIPTION_ERROR = "Описание вещи не может быть пустым или состоять только из пробелов";

    public static void validateBlank(ItemRequestDto itemRequestDto) throws ValidationException {
        if (!Objects.isNull(itemRequestDto.getName()) && itemRequestDto.getName().isBlank()) {
            throw new ValidationException(NULL_NAME_ERROR);
        }
        if (!Objects.isNull(itemRequestDto.getDescription()) && itemRequestDto.getDescription().isBlank()) {
            throw new ValidationException(NULL_DESCRIPTION_ERROR);
        }
    }

}

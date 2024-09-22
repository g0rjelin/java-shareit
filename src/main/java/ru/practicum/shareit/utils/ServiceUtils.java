package ru.practicum.shareit.utils;

import lombok.experimental.UtilityClass;

import java.util.Objects;

@UtilityClass
public class ServiceUtils {

    public static <T> T getDefaultIfNull(T value, T defaultValue) {
        return Objects.isNull(value) ? defaultValue : value;
    }
}

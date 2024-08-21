package ru.practicum.shareit.utils;

public class ServiceUtils {

    public static <T> T getDefaultIfNull(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
}

package ru.practicum.shareit.exception;

public class WrongDateIntervalException extends RuntimeException {
    public WrongDateIntervalException(String message) {
        super(message);
    }
}

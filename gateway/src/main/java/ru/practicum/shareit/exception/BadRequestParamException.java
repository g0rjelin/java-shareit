package ru.practicum.shareit.exception;

public class BadRequestParamException extends RuntimeException {
    public BadRequestParamException(String message) {
        super(message);
    }
}

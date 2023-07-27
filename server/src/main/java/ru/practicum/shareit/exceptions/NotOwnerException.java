package ru.practicum.shareit.exceptions;

public class NotOwnerException extends IllegalArgumentException {
    public NotOwnerException(String message) {
        super(message);
    }
}

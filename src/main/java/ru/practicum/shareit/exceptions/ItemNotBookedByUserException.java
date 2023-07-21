package ru.practicum.shareit.exceptions;

public class ItemNotBookedByUserException extends RuntimeException {
    public ItemNotBookedByUserException(String message) {
        super(message);
    }
}

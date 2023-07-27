package ru.practicum.shareit.booking.dto;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class BookingInputDto {
    LocalDateTime start;
    LocalDateTime end;
    Long itemId;
}

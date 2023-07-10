package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BookingInputDto {
    @NotNull
    private final LocalDateTime start;
    @NotNull
    private final LocalDateTime end;
    @NotNull
    private Long itemId;
}

package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemBookingDto {
    private Long id;
    private Long bookerId;
    private Item item;
    private LocalDateTime start;
    private LocalDateTime end;
}

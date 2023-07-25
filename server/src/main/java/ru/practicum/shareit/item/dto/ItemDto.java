package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.ItemBookingDto;

import java.util.List;


@Data
@NoArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;

    private Long requestId;

    private ItemBookingDto lastBooking;
    private ItemBookingDto nextBooking;

    private List<CommentDto> comments;


}

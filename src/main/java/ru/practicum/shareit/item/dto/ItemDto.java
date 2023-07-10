package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.ItemBookingDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Name is mandatory")
    @NotNull
    private String name;
    @NotBlank(message = "Description is mandatory")
    @NotNull
    private String description;
    @NotNull
    private Boolean available;

    private ItemBookingDto lastBooking;
    private ItemBookingDto nextBooking;

    private List<CommentDto> comments;


}

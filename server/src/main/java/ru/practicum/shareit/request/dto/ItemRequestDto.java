package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@Getter
public class ItemRequestDto {
    private final Long id;

    @NotNull
    private final String description;
    private final LocalDateTime created;
    private final Set<ItemDto> items;
}
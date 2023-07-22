package ru.practicum.shareit.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.HashSet;

public class ItemRequestMapperTest {

    @Test
    void toDtoTest() {
        ItemRequest itemRequest = fillEntity();

        ItemRequestDto itemRequestDto = ItemRequestMapper.toDto(itemRequest);

        Assertions.assertThat(itemRequestDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("description", "itemDescription")
                .hasFieldOrPropertyWithValue("created", itemRequest.getCreated())
                .hasFieldOrProperty("items");
    }

    @Test
    void toItemRequestTest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "itemDescription", LocalDateTime.now(), new HashSet<>());

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, new User());

        Assertions.assertThat(itemRequest)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("description", "itemDescription")
                .hasFieldOrPropertyWithValue("created", itemRequestDto.getCreated())
                .hasFieldOrProperty("items");
    }

    private final ItemRequest fillEntity() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("itemDescription");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setItems(new HashSet<>());
        return itemRequest;
    }
}

package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto editItem(ItemDto itemDto, Long itemId, Long userId);

    ItemDtoWithBooking getItemById(Long itemId, Long userId);

    List<ItemDtoWithBooking> getAllItems(Long userId, Integer from, Integer size);

    List<ItemDto> searchItems(String text, Integer from, Integer size);

    CommentDto addComment(CommentDto commentDto, Long itemId, Long userId);
}

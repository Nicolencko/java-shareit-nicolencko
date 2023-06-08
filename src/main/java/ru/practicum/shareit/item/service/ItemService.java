package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(ItemDto itemDto, Long userId);

    Item editItem(ItemDto itemDto, Long itemId, Long userId);

    Item getItemById(Long itemId);

    List<Item> getAllItems(Long userId);

    List<Item> searchItems(String text);
}

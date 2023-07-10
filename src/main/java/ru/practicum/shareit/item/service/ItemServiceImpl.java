package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Item addItem(ItemDto itemDto, Long userId) {
        return itemRepository.addItem(itemDto, userId);
    }

    @Override
    public Item editItem(ItemDto itemDto, Long itemId, Long userId) {
        return itemRepository.editItem(itemDto, itemId, userId);
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemRepository.getItemById(itemId);
    }

    @Override
    public List<Item> getAllItems(Long userId) {
        return itemRepository.getAllItems(userId);
    }

    @Override
    public List<Item> searchItems(String text) {
        return itemRepository.searchItems(text);
    }
}

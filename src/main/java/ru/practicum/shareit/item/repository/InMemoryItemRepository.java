package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {

    private final UserRepository userRepository;

    private Long itemId = 1L;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item addItem(ItemDto itemDto, Long userId) {
        User user = userRepository.getUserById(userId);

        itemDto.setId(itemId);
        Item item = ItemMapper.toItem(itemDto);
        log.info("Item added: {}", item);
        item.setOwner(user);
        items.put(item.getId(), item);
        itemId++;
        return item;
    }

    @Override
    public Item editItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = items.get(itemId);
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            log.error("User with id {} is not the owner of item with id {}", userId, itemId);
            throw new NotOwnerException("User with id " + userId + " is not the owner of item with id " + itemId);
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        log.info("Item with id {} was edited", itemId);
        items.put(itemId, item);
        return item;
    }

    @Override
    public Item getItemById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getAllItems(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getDescription().toLowerCase().contains(text.toLowerCase()) ||
                        item.getName().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }
}

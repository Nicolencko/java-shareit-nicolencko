package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(UserRepository userRepository, ItemRequestRepository itemRequestRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRequestRepository = itemRequestRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, long userId) {
        User requester = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requester);
        itemRequest.setRequester(requester);

        log.info("Saving item request: {}", itemRequest);

        return ItemRequestMapper.toDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long userId, int from, int size) {
        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException("Page number and size must be positive");
        }
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
        Pageable page = PageRequest.of(from / size, size, Sort.by("created"));
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdIsNot(userId, page).getContent();
        for (ItemRequest itemRequest : itemRequests) {
            List<Item> items = itemRepository.findAllByItemRequest(itemRequest);
            itemRequest.setItems(new HashSet<>(ItemMapper.toDtoList(items)));
        }
        return itemRequests.stream().map(ItemRequestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(long requestId, long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new ItemNotFoundException("Request with id " + requestId + " not found"));
        itemRequest.setItems(new HashSet<>(ItemMapper.toDtoList(itemRepository.findAllByItemRequest(itemRequest))));
        return ItemRequestMapper.toDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAllUserRequests(Long requesterId) {
        userRepository.findById(requesterId).orElseThrow(() -> new UserNotFoundException("User with id " + requesterId + " not found"));

        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequester_Id(requesterId, sort);
        for (ItemRequest itemRequest : itemRequests) {
            List<Item> items = itemRepository.findAllByItemRequest(itemRequest);
            System.out.println(items);
            itemRequest.setItems(new HashSet<>(ItemMapper.toDtoList(items)));
        }

        return itemRequests.stream().map(ItemRequestMapper::toDto).collect(Collectors.toList());
    }
}

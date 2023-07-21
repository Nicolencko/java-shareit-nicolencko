package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, long userId);

    List<ItemRequestDto> getAllRequests(long userId, int from, int size);

    ItemRequestDto getRequestById(long requestId, long userId);

    List<ItemRequestDto> getAllUserRequests(Long requesterId);
}

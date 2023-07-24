package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toOutputItemDto(Item item, List<Comment> comments) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setRequestId(item.getItemRequest() != null ? item.getItemRequest().getId() : null);
        itemDto.setAvailable(item.getAvailable());
        itemDto.setComments(CommentMapper.toDtoList(comments));
        return itemDto;
    }

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setRequestId(item.getItemRequest() != null ? item.getItemRequest().getId() : null);
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    public static ItemDto toOwnerItemDto(Item item, List<Booking> bookings, List<Comment> comments) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setLastBooking(bookings.get(0) == null ? null : BookingMapper.toItemBookingDto(bookings.get(0)));
        itemDto.setNextBooking(bookings.get(1) == null ? null : BookingMapper.toItemBookingDto(bookings.get(1)));
        itemDto.setComments(CommentMapper.toDtoList(comments));
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable()
        );
    }

    public static List<ItemDto> toDtoList(Collection<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}

package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ItemNotBookedByUserException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;
    private final ItemRequestService requestService;


    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(findUserById(userId));
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestService.getRequestById(itemDto.getRequestId(), userId), userRepository.getReferenceById(userId));
            item.setItemRequest(itemRequest);
        }
        item = itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto editItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item with id " + itemId + " not found"));
        if (!item.getOwner().getId().equals(userId)) {
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
        return ItemMapper.toItemDto(itemRepository.save(item));

    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item with id " + itemId + " not found"));
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        if (item.getOwner().getId().equals(userId)) {
            List<Booking> bookings = getLastNextBookings(item);
            return ItemMapper.toOwnerItemDto(item, bookings, comments);
        }
        return ItemMapper.toOutputItemDto(item, comments);
    }

    @Override
    public List<ItemDto> getAllItems(Long userId, Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException("Page number and size must be positive");
        }
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
        Pageable page = PageRequest.of(from / size, size);
        List<Item> items = itemRepository.getAllByOwnerId(userId, page);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            List<Comment> comments = commentRepository.findAllByItemId(item.getId());
            List<Booking> bookings = getLastNextBookings(item);

            itemDtos.add(ItemMapper.toOwnerItemDto(item, bookings, comments));
        }
        return itemDtos;
    }

    @Override
    public List<ItemDto> searchItems(String text, Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException("Page number and size must be positive");
        }
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        Pageable page = PageRequest.of(from / size, size);
        List<Item> items = itemRepository.searchAvailableItemsByKeyword(text, page);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            List<Comment> comments = commentRepository.findAllByItemId(item.getId());

            itemDtos.add(ItemMapper.toOutputItemDto(item, comments));
        }
        return itemDtos;
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item with id " + itemId + " not found"));
        checkIfUserIsBooked(itemId, userId, item);
        Comment comment = CommentMapper.fromDto(commentDto);
        comment.setItem(item);
        comment.setAuthor(findUserById(userId));
        comment.setCreated(LocalDateTime.now());
        log.info("Comment: {}", comment);
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    private void checkIfUserIsBooked(Long itemId, Long bookerId, Item item) {
        List<Booking> bookings = bookingRepository
                .findAllByItemAndBookerIdAndStatusIsAndEndIsBefore(item, bookerId, BookingStatus.APPROVED, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new ItemNotBookedByUserException("Item with id " + itemId + " wasn't booked by user with id " + bookerId);
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
    }

    private List<Booking> getLastNextBookings(Item item) {
        LocalDateTime now = LocalDateTime.now();
        Optional<Booking> lastBooking = bookingRepository.getLastItemBooking(item.getId(), now);
        Optional<Booking> nextBooking = bookingRepository.getNextItemBooking(item.getId(), now);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(null);
        bookings.add(null);
        lastBooking.ifPresent(booking -> bookings.set(0, booking));
        nextBooking.ifPresent(booking -> bookings.set(1, booking));
        return bookings;
    }
}

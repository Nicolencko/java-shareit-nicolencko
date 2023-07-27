package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ItemNotBookedByUserException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.*;
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
import java.util.*;
import java.util.stream.Collectors;

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
    public ItemDtoWithBooking getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item with id " + itemId + " not found"));
        List<ItemBookingDto> bookingItemDtoList = bookingRepository.findByItem_IdAndStatusOrderByStartDesc(itemId,
                        BookingStatus.APPROVED)
                .stream()
                .map(BookingMapper::toItemBookingDto)
                .collect(Collectors.toList());
        List<Comment> comments = commentRepository.findByItem_IdOrderByCreatedDesc(itemId);
        ItemDtoWithBooking itemDto = ItemMapper.itemDtoWithBooking(item);
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentDtos.add(CommentMapper.toDto(comment));
        }

        if (item.getOwner().getId().equals(userId)) {
            setBookings(itemDto, bookingItemDtoList);
        }
        itemDto.setComments(commentDtos);
        return itemDto;
    }

    @Override
    public List<ItemDtoWithBooking> getAllItems(Long userId, Integer from, Integer size) {
        List<ItemDtoWithBooking> itemDtoList = itemRepository.findAll()
                .stream()
                .filter(i -> Objects.equals(i.getOwner().getId(), userId))
                .map(ItemMapper::itemDtoWithBooking)
                .sorted(Comparator.comparing(ItemDtoWithBooking::getId))
                .collect(Collectors.toList());

        List<ItemBookingDto> bookingItemDtoList = bookingRepository.findAllByOwnerIdWithoutPaging(userId,
                        Sort.by(Sort.Direction.DESC, "start"))
                .stream()
                .map(BookingMapper::toItemBookingDto)
                .collect(Collectors.toList());

        List<Comment> comments = commentRepository.findAllByItemIdInOrderByCreatedDesc(
                itemDtoList.stream()
                        .map(ItemDtoWithBooking::getId)
                        .collect(Collectors.toList()));
        itemDtoList.forEach(i -> {
            setBookings(i, bookingItemDtoList);
            setComments(itemDtoList, comments);
        });

        return itemDtoList;
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
        List<Comment> comments = commentRepository.findByItemIn(items);
        for (Item item : items) {
            itemDtos.add(ItemMapper.toOutputItemDto(item, comments));
        }
        return itemDtos;
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item with id " + itemId + " not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));

        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerIdAndStatus(itemId, userId, BookingStatus.APPROVED,
                Sort.by(Sort.Direction.DESC, "start")).orElseThrow(() -> new ItemNotBookedByUserException(
                String.format("Пользователь с id %d не арендовал вещь с id %d", userId, itemId)));

        bookings.stream().filter(booking -> booking.getEnd().isBefore(LocalDateTime.now())).findAny().orElseThrow(() ->
                new ItemNotBookedByUserException(String.format("Пользователь с id %d не может оставлять комментарии вещи " +
                        "с id %d.", userId, itemId)));
        Comment comment = CommentMapper.fromDto(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        log.info("Comment: {}", comment);
        comment = commentRepository.save(comment);
        return CommentMapper.toDto(comment);
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

    private void checkIfUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
    }

    private void setBookings(ItemDtoWithBooking itemDtoWithBooking, List<ItemBookingDto> bookings) {
        itemDtoWithBooking.setLastBooking(bookings.stream()
                .filter(booking -> Objects.equals(booking.getItem().getId(), itemDtoWithBooking.getId()) &&
                        booking.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(ItemBookingDto::getStart)).orElse(null));

        itemDtoWithBooking.setNextBooking(bookings.stream()
                .filter(booking -> Objects.equals(booking.getItem().getId(), itemDtoWithBooking.getId()) &&
                        booking.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(ItemBookingDto::getStart)).orElse(null));
    }

    private void setComments(List<ItemDtoWithBooking> itemDtoList, List<Comment> comments) {
        for (ItemDtoWithBooking itemDto : itemDtoList) {
            itemDto.setComments(comments.stream()
                    .filter(comment -> Objects.equals(comment.getItem().getId(), itemDto.getId()))
                    .map(CommentMapper::toDto)
                    .collect(Collectors.toList()));
        }
    }


}

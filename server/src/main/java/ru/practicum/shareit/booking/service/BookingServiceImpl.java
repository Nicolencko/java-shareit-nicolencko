package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingOutputDto addBooking(BookingInputDto bookingDto, Long userId) {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new BookingDateException("Start date must be before end date");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now()) || bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new BookingDateException("Start and end dates must be in the future");
        }
        if (bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new BookingDateException("Start and end dates must be different");
        }
        Booking booking = BookingMapper.fromBookingInputDto(bookingDto);
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new ItemNotFoundException("Item with id " + bookingDto.getItemId() + " not found"));
        if (item.getAvailable().equals(false)) {
            throw new ItemIsBookedException("Item with id " + bookingDto.getItemId() + " is not available");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotOwnerException("User with id " + userId + " is not allowed to book his own item");
        }
        booking.setBooker(userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found")));
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        log.info("Booking created: {}", booking);
        bookingRepository.save(booking);
        return BookingMapper.toBookingOutputDto(booking);
    }

    @Override
    public BookingOutputDto editBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Booking with id " + bookingId + " not found"));
        Item item = booking.getItem();
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotOwnerException("User with id " + userId + " is not allowed to edit booking with id " + bookingId);
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ItemIsBookedException("Booking with id " + bookingId + " is not waiting for approval");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        log.info("Booking updated: {}", booking);
        bookingRepository.save(booking);
        return BookingMapper.toBookingOutputDto(booking);
    }

    @Override
    public BookingOutputDto getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Booking with id " + bookingId + " not found"));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return BookingMapper.toBookingOutputDto(booking);
        }
        throw new NotOwnerException("User with id " + userId + " is not allowed to get booking with id " + bookingId);
    }

    @Override
    public List<BookingOutputDto> getAllUsersBookings(Long userId, BookingState state, Integer from, Integer size) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
        if (from < 0 || size <= 0) {
            System.out.println("a");
            throw new IllegalArgumentException("Page number and size must be positive");
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();
        Pageable page = PageRequest.of(from / size, size);

        List<Booking> bookings;
        switch (state) {
            case WAITING:
                bookings = bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(userId,
                        BookingStatus.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(userId,
                        BookingStatus.REJECTED, page);
                break;
            case PAST:
                bookings = bookingRepository.findByBooker_IdAndEndBeforeOrderByStartDesc(userId,
                        LocalDateTime.now(), page);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBooker_IdAndStartAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), page);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now(), page);
                break;
            default:
                bookings = bookingRepository.findByBooker_IdOrderByStartDesc(userId, page);
        }

        return bookings.stream().map(BookingMapper::toBookingOutputDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingOutputDto> getAllUsersItemsBookings(Long userId, BookingState state, Integer from, Integer size) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException("Page number and size must be positive");
        }
        LocalDateTime now = LocalDateTime.now();
        Pageable page = PageRequest.of(from / size, size);

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings;
        switch (state) {
            case WAITING:
                bookings = bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(userId,
                        BookingStatus.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(userId,
                        BookingStatus.REJECTED, page);
                break;
            case PAST:
                bookings = bookingRepository.findByItem_Owner_IdAndEndBeforeOrderByStartDesc(userId,
                        LocalDateTime.now(), page);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItem_Owner_IdAndStartAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), page);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now(), page);
                break;
            default:
                bookings = bookingRepository.findByItem_Owner_IdOrderByStartDesc(userId, page);
        }

        bookings.sort(Comparator.comparing(Booking::getStart).reversed());

        return bookings
                .stream()
                .map(BookingMapper::toBookingOutputDto)
                .collect(Collectors.toList());

    }

}

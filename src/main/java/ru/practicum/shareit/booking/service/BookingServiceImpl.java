package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingOutputDto addBooking(BookingInputDto bookingDto, Long userId) {
        System.out.println(bookingDto.getStart());
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            System.out.println("b");
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
        booking.setItem(itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new ItemNotFoundException("Item with id " + bookingDto.getItemId() + " not found")));
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
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
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
        System.out.println(from);
        if (from < 0 || size <= 0) {
            System.out.println("a");
            throw new IllegalArgumentException("Page number and size must be positive");
        }
        LocalDateTime now = LocalDateTime.now();
        Pageable page = PageRequest.of(from / size, size);
        switch (state) {
            case ALL:
                return BookingMapper.toBookingOutputDtoList(bookingRepository.findAllByBookerIdOrderByStartDesc(userId, page));
            case WAITING:
                return BookingMapper.toBookingOutputDtoList(bookingRepository.findAllByBookerIdAndStartIsAfterAndStatusIsOrderByStartDesc(userId, now, BookingStatus.WAITING, page));
            case REJECTED:
                return BookingMapper.toBookingOutputDtoList(bookingRepository.findAllByBookerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.REJECTED, page));
            case CURRENT:
                return BookingMapper.toBookingOutputDtoList(bookingRepository.findAllByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(userId, now, now, page));
            case PAST:
                return BookingMapper.toBookingOutputDtoList(bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, now, page));
            case FUTURE:
                return BookingMapper.toBookingOutputDtoList(bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, now, page));
            default:
                throw new StateNotFoundException("Booking state " + state + " not found");
        }
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
        switch (state) {
            case ALL:
                return BookingMapper.toBookingOutputDtoList(bookingRepository.getAllUsersItemsBookings(userId, page));
            case WAITING:
                return BookingMapper.toBookingOutputDtoList(bookingRepository.getAllWaitingUsersItemsBookings(userId, now, BookingStatus.WAITING, page));
            case REJECTED:
                return BookingMapper.toBookingOutputDtoList(bookingRepository.getAllRejectedUsersItemsBookings(userId, BookingStatus.REJECTED, page));
            case CURRENT:
                return BookingMapper.toBookingOutputDtoList(bookingRepository.getAllCurrentUsersItemsBookings(userId, now, page));
            case PAST:
                return BookingMapper.toBookingOutputDtoList(bookingRepository.getAllPastUsersItemsBookings(userId, now, page));
            case FUTURE:
                return BookingMapper.toBookingOutputDtoList(bookingRepository.getAllFutureUsersItemsBookings(userId, now, BookingStatus.APPROVED, page));
            default:
                throw new StateNotFoundException("Booking state " + state + " not found");
        }

    }

}

package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;

import java.util.List;

public interface BookingService {
    BookingOutputDto addBooking(BookingInputDto bookingInputDto, Long userId);

    BookingOutputDto editBooking(Long userId, Long bookingId, Boolean approved);

    BookingOutputDto getBooking(Long userId, Long bookingId);

    List<BookingOutputDto> getAllUsersBookings(Long userId, BookingState state, Integer from, Integer size);

    List<BookingOutputDto> getAllUsersItemsBookings(Long userId, BookingState state, Integer from, Integer size);
}

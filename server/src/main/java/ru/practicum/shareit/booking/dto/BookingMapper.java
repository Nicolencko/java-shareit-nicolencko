package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
    public static BookingOutputDto toBookingOutputDto(Booking booking) {
        return new BookingOutputDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toItemDto(booking.getItem()),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public static Booking fromBookingInputDto(BookingInputDto bookingDto) {
        return new Booking(
                bookingDto.getStart(),
                bookingDto.getEnd()
        );
    }

    public static List<BookingOutputDto> toBookingOutputDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingOutputDto)
                .collect(Collectors.toList());
    }

    public static ItemBookingDto toItemBookingDto(Booking booking) {
        if (booking == null) {
            return new ItemBookingDto(null, null, null, null, null);
        }
        return new ItemBookingDto(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getItem(),
                booking.getStart(),
                booking.getEnd()
        );
    }
}

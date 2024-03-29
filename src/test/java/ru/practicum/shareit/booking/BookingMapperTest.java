package ru.practicum.shareit.booking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

class BookingMapperTest {

    @Test
    void toBookingTest() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);

        BookingInputDto inputDto = new BookingInputDto(start, end, 1L);

        Booking booking = BookingMapper.fromBookingInputDto(inputDto);

        Assertions.assertThat(booking)
                .hasFieldOrPropertyWithValue("id", null)
                .hasFieldOrPropertyWithValue("start", start)
                .hasFieldOrPropertyWithValue("end", end);
    }

    @Test
    void toBookingDtoTest() {
        Booking booking = fillEntity();

        BookingOutputDto bookingDto = BookingMapper.toBookingOutputDto(booking);

        Assertions.assertThat(bookingDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", booking.getStart())
                .hasFieldOrPropertyWithValue("end", booking.getEnd())
                .hasFieldOrProperty("item")
                .hasFieldOrProperty("booker")
                .hasFieldOrPropertyWithValue("status", BookingStatus.APPROVED);
        Assertions.assertThat(bookingDto.getItem())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "itemName");
        Assertions.assertThat(bookingDto.getBooker())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "userName");
    }

    @Test
    void toBookingDtosTest() {
        List<Booking> bookings = List.of(fillEntity());

        List<BookingOutputDto> bookingDtos = BookingMapper.toBookingOutputDtoList(bookings);

        Assertions.assertThat(bookingDtos)
                .hasSize(1);
    }

    private Booking fillEntity() {
        Item item = new Item();
        item.setId(1L);
        item.setName("itemName");

        User user = new User();
        user.setId(1L);
        user.setName("userName");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setItem(item);
        booking.setBooker(user);

        return booking;
    }
}
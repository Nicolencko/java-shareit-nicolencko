package ru.practicum.shareit.booking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@Sql(value = {"/schema.sql", "/user-item-test.sql", "/items-booking-test.sql", "/bookings-test.sql"})
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    private final int from = 0;
    private final int size = 10;
    private final long bookerId = 2L;
    private final Pageable pageable = PageRequest.of(
            from,
            size
    );

    @Test
    void findAllByBookerTest() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId, pageable);
        Assertions.assertThat(bookings).hasSize(3);
    }


    @Test
    void findAllCurrentByBookerTest() {
        List<Booking> bookings = bookingRepository.getAllCurrentUsersItemsBookings(bookerId, LocalDateTime.now(), pageable);
        Assertions.assertThat(bookings).isEmpty();
    }

}
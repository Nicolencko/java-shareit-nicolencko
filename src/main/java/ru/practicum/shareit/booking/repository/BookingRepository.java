package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable page);

    List<Booking> findAllByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(Long userId,
                                                                                 LocalDateTime endDateTime,
                                                                                 LocalDateTime startDateTime, Pageable page);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime endDateTime, Pageable page);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long userId,
                                                                   LocalDateTime startDateTime, Pageable page);

    List<Booking> findAllByBookerIdAndStartIsAfterAndStatusIsOrderByStartDesc(Long userId,
                                                                              LocalDateTime startDateTime,
                                                                              BookingStatus bookingStatus, Pageable page);

    List<Booking> findAllByBookerIdAndStatusIsOrderByStartDesc(Long userId, BookingStatus status, Pageable page);

    @Query("select b from Booking b Inner join Item i on b.item.id = i.id where i.owner.id = ?1 order by b.start desc")
    List<Booking> getAllUsersItemsBookings(Long userId, Pageable page);

    @Query("select b from Booking b Inner join Item i on b.item.id = i.id where i.owner.id = ?1 " +
            "and b.start > ?2 and b.status = ?3 order by b.start desc")
    List<Booking> getAllWaitingUsersItemsBookings(Long userId, LocalDateTime startDateTime, BookingStatus bookingStatus, Pageable page);

    @Query("select b from Booking b Inner join Item i on b.item.id = i.id where i.owner.id = ?1 " +
            "and b.status = ?2 order by b.start desc")
    List<Booking> getAllRejectedUsersItemsBookings(Long userId, BookingStatus bookingStatus, Pageable page);

    @Query("select b from Booking b Inner join Item i on b.item.id = i.id where i.owner.id = ?1 " +
            "and ?2 between b.start and b.end order by b.start desc")
    List<Booking> getAllCurrentUsersItemsBookings(Long userId, LocalDateTime endDateTime, Pageable page);

    @Query("select b from Booking b Inner join Item i on b.item.id = i.id where i.owner.id = ?1 " +
            "and b.end < ?2 order by b.start desc")
    List<Booking> getAllPastUsersItemsBookings(Long userId, LocalDateTime startDateTime, Pageable page);

    @Query("select b from Booking b Inner join Item i on b.item.id = i.id where i.owner.id = ?1 " +
            "and b.start > ?2 order by b.start desc")
    List<Booking> getAllFutureUsersItemsBookings(Long userId, LocalDateTime startDateTime, BookingStatus bookingStatus, Pageable page);

    @Query(value = "SELECT * FROM bookings b INNER JOIN items i on i.id = b.item_id "
            + "WHERE b.item_id = ?1 AND b.start_time < ?2 ORDER BY b.end_time DESC LIMIT 1",
            nativeQuery = true)
    Optional<Booking> getLastItemBooking(Long itemId, LocalDateTime nowTime);

    @Query(value = "SELECT * FROM bookings b INNER JOIN items i on i.id = b.item_id "
            + "WHERE b.status <> 'REJECTED' and b.item_id = ?1 and b.start_time > ?2 ORDER BY b.start_time ASC LIMIT 1",
            nativeQuery = true)
    Optional<Booking> getNextItemBooking(Long itemId, LocalDateTime nowTime);

    List<Booking> findAllByItemAndBookerIdAndStatusIsAndEndIsBefore(Item item,
                                                                    Long userId,
                                                                    BookingStatus bookingStatus,
                                                                    LocalDateTime nowTime);
}

package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Optional;

@JsonTest
public class BookingOutputDtoJsonTest {
    @Autowired
    private JacksonTester<BookingOutputDto> json;

    @Test
    @SneakyThrows
    void bookingOutputDtoTest() {
        ItemDto itemDto = new ItemDto();
        UserDto userDto = new UserDto(1L, "name", "email@email.com");

        BookingOutputDto bookingDto = new BookingOutputDto(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), itemDto, userDto, BookingStatus.APPROVED);
        Optional<JsonContent<BookingOutputDto>> result = Optional.of(json.write(bookingDto));

        Assertions.assertThat(result)
                .isPresent()
                .hasValueSatisfying(i -> {
                    Assertions.assertThat(i)
                            .extractingJsonPathStringValue("$.start");
                    Assertions.assertThat(i)
                            .extractingJsonPathStringValue("$.end");
                    Assertions.assertThat(i)
                            .extractingJsonPathNumberValue("$.itemId", 1L);
                });
    }
}

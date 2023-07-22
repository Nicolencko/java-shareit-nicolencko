package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.time.LocalDateTime;
import java.util.Optional;

@JsonTest
public class BookingInputDtoJsonTest {
    @Autowired
    private JacksonTester<BookingInputDto> json;

    @Test
    @SneakyThrows
    void bookingInputDtoTest() {
        BookingInputDto bookingDto = new BookingInputDto(LocalDateTime.now(), LocalDateTime.now().plusHours(1), 1L);
        Optional<JsonContent<BookingInputDto>> result = Optional.of(json.write(bookingDto));

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

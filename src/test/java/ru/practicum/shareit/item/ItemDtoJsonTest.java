package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    @SneakyThrows
    void itemDtoTest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("text");
        itemDto.setAvailable(true);
        itemDto.setLastBooking(new ItemBookingDto(1L, 1L, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)));
        itemDto.setName("user");
        itemDto.setNextBooking(new ItemBookingDto(2L, 1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2)));
        itemDto.setComments(List.of());
        itemDto.setRequestId(1L);
        Optional<JsonContent<ItemDto>> result = Optional.of(json.write(itemDto));

        Assertions.assertThat(result)
                .isPresent()
                .hasValueSatisfying(i -> {
                    Assertions.assertThat(i)
                            .extractingJsonPathNumberValue("$.id").isEqualTo(1);
                    Assertions.assertThat(i)
                            .extractingJsonPathStringValue("$.name").isEqualTo("user");
                    Assertions.assertThat(i)
                            .extractingJsonPathStringValue("$.description").isEqualTo("text");
                    Assertions.assertThat(i)
                            .extractingJsonPathBooleanValue("$.available").isEqualTo(true);
                    Assertions.assertThat(i)
                            .hasJsonPathValue("lastBooking");
                    Assertions.assertThat(i)
                            .hasJsonPathValue("nextBooking");
                    Assertions.assertThat(i)
                            .hasJsonPathArrayValue("comments");
                    Assertions.assertThat(i)
                            .extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
                });
    }
}

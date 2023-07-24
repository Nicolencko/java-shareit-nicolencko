package ru.practicum.shareit.request;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;


@JsonTest
public class ItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    @SneakyThrows
    void itemRequestDtoTest() {
        LocalDateTime created = LocalDateTime.now().minusHours(1);
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "text", created, Set.of());
        Optional<JsonContent<ItemRequestDto>> result = Optional.of(json.write(itemRequestDto));

        Assertions.assertThat(result)
                .isPresent()
                .hasValueSatisfying(i -> {
                    Assertions.assertThat(i)
                            .extractingJsonPathNumberValue("$.id").isEqualTo(1);
                    Assertions.assertThat(i)
                            .extractingJsonPathStringValue("$.description").isEqualTo("text");
                    Assertions.assertThat(i)
                            .hasJsonPath("$.created");
                    Assertions.assertThat(i)
                            .hasJsonPathArrayValue("items");
                });
    }
}

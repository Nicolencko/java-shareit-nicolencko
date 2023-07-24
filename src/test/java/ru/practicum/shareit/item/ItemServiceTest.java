package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceTest {
    private final ItemService itemService;

    @Test
    @Order(0)
    @Sql(value = {"/schema.sql", "/user-item-test.sql"})
    void createTest() {
        ItemDto itemCreateDto = new ItemDto();
        itemCreateDto.setName("item");
        itemCreateDto.setDescription("users 1 item");
        itemCreateDto.setAvailable(false);
        Optional<ItemDto> itemDto = Optional.of(itemService.addItem(itemCreateDto, 1L));

        Assertions.assertThat(itemDto)
                .isPresent()
                .hasValueSatisfying(i -> {
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("id", 1L);
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("name", "item");
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("description", "users 1 item");
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("available", false);
                });
    }

    @Test
    @Order(1)
    void updateAvailableTest() {
        ItemDto itemCreateDto = new ItemDto();
        itemCreateDto.setAvailable(true);
        Optional<ItemDto> itemDto = Optional.of(itemService.editItem(itemCreateDto, 1L, 1L));

        Assertions.assertThat(itemDto)
                .isPresent()
                .hasValueSatisfying(i -> {
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("id", 1L);
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("name", "item");
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("description", "users 1 item");
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("available", true);
                });
    }

    @Test
    @Order(2)
    void updateDescriptionTest() {
        ItemDto itemCreateDto = new ItemDto();
        itemCreateDto.setDescription("users 1 updated item");
        Optional<ItemDto> itemDto = Optional.of(itemService.editItem(itemCreateDto, 1L, 1L));

        Assertions.assertThat(itemDto)
                .isPresent()
                .hasValueSatisfying(i -> {
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("id", 1L);
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("name", "item");
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("description", "users 1 updated item");
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("available", true);
                });
    }

    @Test
    @Order(3)
    void updateNameTest() {
        ItemDto itemCreateDto = new ItemDto();
        itemCreateDto.setName("updated item");
        Optional<ItemDto> itemDto = Optional.of(itemService.editItem(itemCreateDto, 1L, 1L));

        Assertions.assertThat(itemDto)
                .isPresent()
                .hasValueSatisfying(i -> {
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("id", 1L);
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("name", "updated item");
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("description", "users 1 updated item");
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("available", true);
                });
    }

    @Test
    @Order(4)
    void updateUserIdIncorrectTest() {
        ItemDto itemCreateDto = new ItemDto();
        itemCreateDto.setName("item2");
        itemCreateDto.setDescription("users 100 item");
        itemCreateDto.setAvailable(false);

        NotOwnerException exception = assertThrows(NotOwnerException.class,
                () -> itemService.editItem(itemCreateDto, 1L, 100L));

        Assertions.assertThat(exception)
                .hasMessage("User with id " + 100L + " is not the owner of item with id " + 1L);
    }

    @Test
    @Order(5)
    void updateItemIdIncorrectTest() {
        ItemDto itemCreateDto = new ItemDto();
        itemCreateDto.setName("item1000");
        itemCreateDto.setDescription("users 1 item");
        itemCreateDto.setAvailable(false);

        assertThrows(ItemNotFoundException.class, () -> itemService.editItem(itemCreateDto, 10L, 1L));
    }

    @Test
    @Order(6)
    void getByWrongIdTest() {
        assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(100L, 1L));
    }

    @Test
    @Order(7)
    void getByIdForOwnerTest() {
        Optional<ItemDto> itemDto = Optional.of(itemService.getItemById(1L, 1L));

        Assertions.assertThat(itemDto)
                .isPresent()
                .hasValueSatisfying(i -> {
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("id", 1L);
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("name", "updated item");
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("description", "users 1 updated item");
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("available", true);
                    Assertions.assertThat(i).hasFieldOrProperty("lastBooking");
                    Assertions.assertThat(i).hasFieldOrProperty("nextBooking");
                    Assertions.assertThat(i).hasFieldOrProperty("comments");
                });
    }

    @Test
    @Order(8)
    void getByIdNotForOwnerTest() {
        Optional<ItemDto> itemDto = Optional.of(itemService.getItemById(1L, 3L));

        Assertions.assertThat(itemDto)
                .isPresent()
                .hasValueSatisfying(i -> {
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("id", 1L);
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("name", "updated item");
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("description", "users 1 updated item");
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("available", true);
                    Assertions.assertThat(i.getLastBooking()).isNull();
                    Assertions.assertThat(i).hasFieldOrProperty("comments");
                });
    }

    @Test
    @Order(9)
    void getAllByTextTest() {
        List<ItemDto> items = itemService.searchItems("item", 0, 10);

        Assertions.assertThat(items)
                .hasSize(1);

        Assertions.assertThat(items.get(0))
                .hasFieldOrPropertyWithValue("id", 1L);
    }
}

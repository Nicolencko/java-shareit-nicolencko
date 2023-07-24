package ru.practicum.shareit.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

class ItemMapperTest {

    @Test
    void toItemTest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setRequestId(1L);
        itemDto.setAvailable(true);

        Item item = ItemMapper.toItem(itemDto);

        Assertions.assertThat(item)
                .hasFieldOrPropertyWithValue("name", "name")
                .hasFieldOrPropertyWithValue("description", "description")
                .hasFieldOrPropertyWithValue("available", true);
    }

    @Test
    void toItemDto() {
        Item item = fillEntity();

        ItemDto itemDto = ItemMapper.toItemDto(item);

        Assertions.assertThat(itemDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "itemName")
                .hasFieldOrPropertyWithValue("description", "itemDescription")
                .hasFieldOrPropertyWithValue("available", true)
                .hasFieldOrProperty("comments")
                .hasFieldOrPropertyWithValue("requestId", null);
    }

    @Test
    void itemDtoForOwnerTest() {
        Item item = fillEntity();
        Booking booking1 = new Booking();
        booking1.setBooker(new User(1L, "user", "email@email.com"));

        Booking booking2 = new Booking();
        booking2.setBooker(new User(2L, "user1", "email1@email.com"));

        Comment comment = new Comment();
        comment.setAuthor(new User(3L, "user2", "email2@email.com"));

        comment.setItem(item);
        List<Booking> bookings = List.of(booking1, booking2);
        List<Comment> comments = List.of(comment);

        Optional<ItemDto> itemDto = Optional.of(ItemMapper.toOwnerItemDto(item, bookings, comments));

        Assertions.assertThat(itemDto)
                .isPresent()
                .hasValueSatisfying(i -> {
                    Assertions.assertThat(i)
                            .hasFieldOrPropertyWithValue("id", 1L)
                            .hasFieldOrPropertyWithValue("name", "itemName")
                            .hasFieldOrPropertyWithValue("description", "itemDescription")
                            .hasFieldOrPropertyWithValue("available", true)
                            .hasFieldOrProperty("lastBooking")
                            .hasFieldOrProperty("nextBooking")
                            .hasFieldOrProperty("comments")
                            .hasFieldOrProperty("requestId");
                    Assertions.assertThat(i.getLastBooking());
                    Assertions.assertThat(i.getNextBooking());
                });
    }

    private Item fillEntity() {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("user@mail.com");

        Item item = new Item();
        item.setId(1L);
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setOwner(user);

        return item;
    }
}
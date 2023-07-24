package ru.practicum.shareit.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;


class UserMapperTest {

    @Test
    void toUserTest() {
        UserDto userDto = new UserDto(1L, "userName", "email@email.com");

        User user = UserMapper.fromUserDto(userDto);

        Assertions.assertThat(user)
                .hasFieldOrPropertyWithValue("name", "userName")
                .hasFieldOrPropertyWithValue("email", "email@email.com");
    }

    @Test
    void toUserDtoTest() {
        User user = fillEntity();

        UserDto userDto = UserMapper.toUserDto(user);

        Assertions.assertThat(userDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "name")
                .hasFieldOrPropertyWithValue("email", "email@email.com");
    }

    private User fillEntity() {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("email@email.com");

        return user;
    }
}

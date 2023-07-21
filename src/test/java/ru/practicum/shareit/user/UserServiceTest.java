package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@RunWith(SpringRunner.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {
    private final UserService userService;

    @Test
    @Order(0)
    @Sql(value = {"/schema.sql"})
    void createTest() {
        User userCreate = new User(1L, "user", "email@email.com");

        Optional<User> user = Optional.of(userService.addUser(userCreate));

        assertThat(user)
                .isPresent()
                .hasValueSatisfying(f -> {
                            assertThat(f).hasFieldOrPropertyWithValue("id", 1L);
                            assertThat(f).hasFieldOrPropertyWithValue("name", "user");
                            assertThat(f).hasFieldOrPropertyWithValue("email", "email@email.com");
                        }
                );
    }

    @Test
    @Order(1)
    void updateTest() {
        User userCreate = new User(1L, "userUpdated", "emailUpdated@email.com");
        Optional<User> user = Optional.of(userService.editUser(userCreate, 1L));

        assertThat(user)
                .isPresent()
                .hasValueSatisfying(f -> {
                            assertThat(f).hasFieldOrPropertyWithValue("id", 1L);
                            assertThat(f).hasFieldOrPropertyWithValue("name", "userUpdated");
                            assertThat(f).hasFieldOrPropertyWithValue("email", "emailUpdated@email.com");
                        }
                );
    }

    @Test
    @Order(2)
    void getByIdCorrectTest() {
        Optional<User> user = Optional.of(userService.getUserById(1L));

        assertThat(user)
                .isPresent()
                .hasValueSatisfying(f -> {
                            assertThat(f).hasFieldOrPropertyWithValue("id", 1L);
                            assertThat(f).hasFieldOrPropertyWithValue("name", "userUpdated");
                            assertThat(f).hasFieldOrPropertyWithValue("email", "emailUpdated@email.com");
                        }
                );
    }

    @Test
    @Order(3)
    void getByIdIncorrectTest() {
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(2023L));
    }

    @Test
    @Order(4)
    void getAllTest() {
        User userCreate = new User(2L, "user1", "email1@email.com");
        userService.addUser(userCreate);
        List<User> users = (List<User>) userService.getAllUsers();
        assertThat(users)
                .hasSize(2)
                .map(User::getId)
                .contains(1L, 2L);
    }

    @Test
    @Order(5)
    void deleteByIdTest() {
        userService.deleteUser(1L);
        List<User> users = (List<User>) userService.getAllUsers();

        assertThat(users)
                .hasSize(1)
                .map(User::getId)
                .contains(2L);
    }

}
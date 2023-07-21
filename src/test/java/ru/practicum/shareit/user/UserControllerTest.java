package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    @MockBean
    private final UserService userService;

    @SneakyThrows
    @Test
    void getById_correctUser_thenReturnOk() {
        long userId = 1L;
        mockMvc.perform(get("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getUserById(userId);
    }

    @SneakyThrows
    @Test
    void getById_incorrectUser_thenReturnNotFound() {
        long userId = 1L;
        doThrow(new UserNotFoundException("user with id " + userId + " not found")).when(userService).getUserById(userId);
        mockMvc.perform(get("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(userService).getUserById(userId);
    }

    @SneakyThrows
    @Test
    void getAll_correctUsers_thenReturnOk() {
        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getAllUsers();
    }

    @SneakyThrows
    @Test
    void create_correctUser_thenReturnOk() {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("email@email.com");
        UserDto userDto = new UserDto(user.getId(), user.getName(), user.getEmail());
        when(userService.addUser(any())).thenReturn(user);
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).addUser(any());
    }

    @SneakyThrows
    @Test
    void update_correctUser_thenReturnOk() {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("email@email.com");
        UserDto userDto = new UserDto(user.getId(), user.getName(), user.getEmail());
        when(userService.editUser(any(), any())).thenReturn(user);
        mockMvc.perform(patch("/users/{userId}", user.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).editUser(any(), any());
    }

    @SneakyThrows
    @Test
    void delete_correctUser_thenReturnOk() {
        long userId = 1L;
        mockMvc.perform(delete("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).deleteUser(userId);
    }

    @SneakyThrows
    @Test
    void delete_incorrectUser_thenReturnNotFound() {
        long userId = 1L;
        doThrow(new UserNotFoundException("user with id " + userId + " not found")).when(userService).deleteUser(userId);
        mockMvc.perform(delete("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(userId);
    }


}
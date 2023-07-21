package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;

    @SneakyThrows
    @Test
    void getById_correctUser_thenReturnOk() {
        long userId = 1L;
        long bookingId = 1L;
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService).getBooking(userId, bookingId);
    }

    @SneakyThrows
    @Test
    void getAllByBooker_withoutStateAndPagination_thenReturnOk() {
        long userId = 1L;
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService).getAllUsersBookings(1L, BookingState.ALL, 0, 10);
    }


    @SneakyThrows
    @Test
    void getAllByOwner_WAITING_thenReturnBadRequest() {
        long userId = 1L;
        BookingState state = BookingState.WAITING;
        mockMvc.perform(get("/bookings/owner?state={state}", state)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService).getAllUsersItemsBookings(1L, BookingState.WAITING, 0, 10);
    }

    @SneakyThrows
    @Test
    void create_allCorrect_thenReturnOk() {
        long userId = 1L;
        BookingInputDto bookingInputDto = new BookingInputDto(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), 1L);
        BookingOutputDto bookingDto = new BookingOutputDto(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), new ItemDto(), new UserDto(1L, "name", "email@email.com"), BookingStatus.WAITING);

        when(bookingService.addBooking(any(), anyLong())).thenReturn(bookingDto);
        String content = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingInputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(bookingDto), content);
    }


    @SneakyThrows
    @Test
    void confirm_allCorrect_thenReturnOk() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;
        mockMvc.perform(patch("/bookings/{bookingId}?approved={approved}", bookingId, approved)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService).editBooking(bookingId, userId, approved);
    }
}
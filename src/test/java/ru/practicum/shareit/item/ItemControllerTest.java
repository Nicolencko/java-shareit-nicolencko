package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    @MockBean
    private final ItemService itemService;

    @SneakyThrows
    @Test
    void getById_correctUser_thenReturnOk() {
        long itemId = 1L;
        long userId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(itemService).getItemById(itemId, userId);
    }

    @SneakyThrows
    @Test
    void getById_unCorrectUser_thenReturnBadRequest() {
        long itemId = 1L;
        long userId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(itemService, Mockito.never()).getItemById(itemId, userId);
    }

    @SneakyThrows
    @Test
    void getAllByUserId_withoutPaginationParams_thenReturnOk() {
        long userId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(itemService).getAllItems(userId, 0, 10);
    }

    @SneakyThrows
    @Test
    void getAllByUserId_withPaginationParams_thenReturnOk() {
        long userId = 1L;
        int from = 3;
        int size = 2;
        mockMvc.perform(MockMvcRequestBuilders.get("/items?from={from}&size={size}", from, size)
                        .header("X-Sharer-User-Id", userId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(itemService).getAllItems(userId, 3, 2);
    }

    @SneakyThrows
    @Test
    void getAllByText_withoutPaginationParams_thenReturnOk() {
        String text = "java forever";
        mockMvc.perform(MockMvcRequestBuilders.get("/items/search?text={text}", text))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(itemService).searchItems(text, 0, 10);
    }

    @SneakyThrows
    @Test
    void create_allCorrect_thenReturnOk() {
        long userId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("item");
        itemDto.setDescription("item description");
        itemDto.setAvailable(false);
        itemDto.setRequestId(null);


        Mockito.when(itemService.addItem(ArgumentMatchers.any(), ArgumentMatchers.anyLong())).thenReturn(itemDto);
        String content = mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(itemDto), content);
    }

    @SneakyThrows
    @Test
    void create_IncorrectItemDto_thenReturnOk() {
        long userId = 1L;
        ItemDto itemDto = new ItemDto();

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(itemService, Mockito.never()).addItem(ArgumentMatchers.any(), ArgumentMatchers.anyLong());
    }

    @SneakyThrows
    @Test
    void update_allCorrect_thenReturnOk() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("item");
        itemDto.setDescription("item description");
        itemDto.setAvailable(false);
        itemDto.setRequestId(null);

        Mockito.when(itemService.editItem(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(itemDto);
        String content = mockMvc.perform(MockMvcRequestBuilders.patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(itemDto), content);
    }
}
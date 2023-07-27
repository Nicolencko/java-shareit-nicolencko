package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    @NotBlank
    @NotNull
    private String text;
    private ItemIncomeDto item;
    private String authorName;
    private LocalDateTime created;
}

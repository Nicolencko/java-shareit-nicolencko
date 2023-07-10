package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
public class ItemDto {
    @NotBlank(message = "Name is mandatory")
    @NotNull
    private String name;
    @NotBlank(message = "Description is mandatory")
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
    private Long id;

}

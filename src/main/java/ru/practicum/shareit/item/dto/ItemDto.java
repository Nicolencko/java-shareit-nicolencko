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
    String name;
    @NotBlank(message = "Description is mandatory")
    @NotNull
    String description;
    @NotNull
    Boolean available;
    Long id;

}

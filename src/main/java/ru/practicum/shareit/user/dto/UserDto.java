package ru.practicum.shareit.user.dto;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
public class UserDto {
    Long id;
    @NotBlank(message = "Name is mandatory")
    String name;
    @Email(message = "Email should be valid")
    String email;
}

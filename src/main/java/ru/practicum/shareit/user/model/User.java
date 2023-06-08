package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class User {
    Long id;

    @NotNull
    String name;

    @Email
    @NotNull
    String email;
}

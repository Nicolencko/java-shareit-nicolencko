package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    User addUser(User user);

    User getUserById(Long id);

    User editUser(User user, Long userId);

    void deleteUser(Long id);

    Collection<User> getAllUsers();

}

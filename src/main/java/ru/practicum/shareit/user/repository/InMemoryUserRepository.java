package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@Slf4j
public class InMemoryUserRepository implements UserRepository {
    private Long userId = 1L;
    private final Map<Long, User> users = new HashMap<>();

    private final Set<String> emails = new HashSet<>();

    @Override
    public User addUser(User user) {
        if (emails.contains(user.getEmail())) {
            log.error("User with email {} already exists", user.getEmail());
            throw new DuplicateEmailException("User with email " + user.getEmail() + " already exists");
        }
        user.setId(userId);
        users.put(userId, user);
        emails.add(user.getEmail());
        userId++;
        log.info("User with email {} was added", user.getEmail());
        return user;
    }

    @Override
    public User getUserById(Long id) {
        if (!users.containsKey(id)) {
            log.error("User with id {} does not exist", id);
            throw new UserNotFoundException("User with id " + id + " does not exist");
        }
        return users.get(id);
    }

    @Override
    public User editUser(User user, Long userId) {
        User userToEdit = users.get(userId);

        if (user.getName() != null) {
            userToEdit.setName(user.getName());
        }
        if (user.getEmail() != null) {
            if (!user.getEmail().equals(userToEdit.getEmail()) && emails.contains(user.getEmail())) {
                log.error("User with email {} already exists", user.getEmail());
                throw new DuplicateEmailException("User with email " + user.getEmail() + " already exists");
            }
            emails.remove(userToEdit.getEmail());
            emails.add(user.getEmail());
            userToEdit.setEmail(user.getEmail());
        }
        log.info("User with id {} was edited", userId);
        return userToEdit;
    }

    @Override
    public void deleteUser(Long id) {
        if (!users.containsKey(id)) {
            log.error("User with id {} does not exist", id);
            throw new UserNotFoundException("User with id " + id + " does not exist");
        }
        User userToDelete = users.get(id);
        emails.remove(userToDelete.getEmail());
        users.remove(id);
        log.info("User with id {} was deleted", id);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }
}

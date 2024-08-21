package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    User getUserById(Long userId);

    UserDto findUserById(Long id);

    UserDto create(User newUser);

    UserDto update(Long id, User updUser);

    void delete(Long id);

}

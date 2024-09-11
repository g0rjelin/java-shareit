package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    User getUserById(Long userId);

    UserDto findUserById(Long id);

    UserDto create(UserDto newUserDto);

    UserDto update(Long id, UserDto updUserDto);

    void delete(Long id);

}

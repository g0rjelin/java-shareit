package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UniqueConstraintException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.validation.UserValidator;
import ru.practicum.shareit.utils.ServiceUtils;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    final UserRepository userRepository;

    static final String USER_NOT_FOUND_MSG = "Пользователь с id = %d не найден";
    static final String DUPLICATE_EMAIL_ERROR = "Электронная почта %s уже используется";


    @Override
    public UserDto findUserById(Long id) {
        return UserMapper.toUserDto(getUserById(id));
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND_MSG, userId)));
    }

    @Override
    public UserDto create(User newUser) {
        UserValidator.validateFormat(newUser);
        checkUniqueEmail(newUser.getId(), newUser.getEmail());
        return UserMapper.toUserDto(userRepository.create(newUser));
    }

    @Override
    public UserDto update(Long id, User updUser) {
        User oldUser = getUserById(id);
        UserValidator.validateFormat(updUser);
        checkUniqueEmail(id, updUser.getEmail());
        oldUser.setName(ServiceUtils.getDefaultIfNull(updUser.getName(), oldUser.getName()));
        oldUser.setEmail(ServiceUtils.getDefaultIfNull(updUser.getEmail(), oldUser.getEmail()));
        return UserMapper.toUserDto(userRepository.update(oldUser));
    }

    @Override
    public void delete(Long id) {
        getUserById(id);
        userRepository.delete(id);
    }

    private void checkUniqueEmail(Long id, String email) {
        if (userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(id))
                .anyMatch(u -> u.getEmail().equals(email))) {
            throw new UniqueConstraintException(String.format(DUPLICATE_EMAIL_ERROR, email));
        }
    }
}

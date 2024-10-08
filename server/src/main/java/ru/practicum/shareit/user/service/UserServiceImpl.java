package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UniqueConstraintException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    final UserRepository userRepository;

    static final String DUPLICATE_EMAIL_ERROR = "Электронная почта %s уже используется";

    @Override
    public UserDto findUserById(Long id) {
        return UserMapper.toUserDto(userRepository.getUserById(id));
    }

    @Override
    public UserDto create(UserDto newUserDto) {
        checkUniqueEmail(newUserDto.getEmail());
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(newUserDto)));
    }

    @Override
    public UserDto update(Long id, UserDto updUserDto) {
        User oldUser = userRepository.getUserById(id);
        if (!Objects.isNull(updUserDto.getEmail()) && !updUserDto.getEmail().equals(oldUser.getEmail())) {
            checkUniqueEmail(updUserDto.getEmail());
        }
        oldUser.setName(Objects.requireNonNullElse(updUserDto.getName(), oldUser.getName()));
        oldUser.setEmail(Objects.requireNonNullElse(updUserDto.getEmail(), oldUser.getEmail()));
        return UserMapper.toUserDto(userRepository.save(oldUser));
    }

    @Override
    public void delete(Long id) {
        User delUser = userRepository.getUserById(id);
        userRepository.delete(delUser);
    }

    private void checkUniqueEmail(String email) {
        if (userRepository.findUserByEmail(email).isPresent()) {
            throw new UniqueConstraintException(String.format(DUPLICATE_EMAIL_ERROR, email));
        }
    }
}

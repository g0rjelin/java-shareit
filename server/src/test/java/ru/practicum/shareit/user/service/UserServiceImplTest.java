package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UniqueConstraintException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {

    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;

    User user;
    User newUser;
    UserDto userDto;
    UserDto newUserDto;

    @BeforeEach
    void UserServiceTestSetUp() {
        Long id = 1L;
        String name = "test user";
        String email = "test@test.com";
        user = new User(id, name, email);
        userDto = UserDto.builder().id(id).name(name).email(email).build();
        newUser = User.builder().name(name).email(email).build();
        newUserDto = UserDto.builder().name(name).email(email).build();
    }

    @Test
    void findUserById_shouldReturnUserDto_whenUserFound() {
        when(userRepository.getUserById(user.getId()))
                .thenReturn(user);
        UserDto expectedUserDto = userDto;

        UserDto actualUserDto = userService.findUserById(user.getId());

        assertNotNull(actualUserDto);
        assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    void findUserById_shouldReturnNotFoundException_whenUserNotFound() {
        when(userRepository.getUserById(user.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> userService.findUserById(user.getId()));
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void createUser_shouldCreateUserDto() {
        when(userRepository.save(newUser)).thenReturn(user);
        UserDto expectedUserDto = userDto;

        UserDto actualUserDto = userService.create(newUserDto);

        assertNotNull(actualUserDto);
        assertEquals(expectedUserDto, actualUserDto);
        assertEquals(expectedUserDto.getName(), actualUserDto.getName());
        assertEquals(expectedUserDto.getEmail(), actualUserDto.getEmail());
    }

    @Test
    void createUser_shouldThrowException_whenUserWithSameEmailAlreadyExists() {
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThrows(UniqueConstraintException.class, () -> userService.create(newUserDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_shouldUpdateUserDto() {
        String newName = "new name";
        String newEmail = "new@email.com";
        UserDto updUserDto = UserDto.builder().name(newName).email(newEmail).build();
        User updUser = User.builder().id(user.getId()).name(newName).email(newEmail).build();
        when(userRepository.getUserById(user.getId()))
                .thenReturn(user);
        when(userRepository.save(updUser)).thenReturn(updUser);
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.empty());
        UserDto expectedUserDto = UserDto.builder().id(user.getId()).name(newName).email(newEmail).build();

        UserDto actualUserDto = userService.update(user.getId(), updUserDto);

        assertNotNull(actualUserDto);
        assertEquals(expectedUserDto, actualUserDto);
        assertEquals(expectedUserDto.getId(), actualUserDto.getId());
        assertEquals(expectedUserDto.getName(), actualUserDto.getName());
        assertEquals(expectedUserDto.getEmail(), actualUserDto.getEmail());
    }

    @Test
    void updateUser_shouldUpdateNameUserDto_whenUserFound() {
        String newName = "new name";
        UserDto updUserDto = UserDto.builder().name(newName).build();
        User updUser = User.builder().id(user.getId()).name(newName).email(user.getEmail()).build();
        user.setName(newName);
        when(userRepository.getUserById(user.getId())).thenReturn(user);
        when(userRepository.save(updUser)).thenReturn(user);
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.empty());
        UserDto expectedUserDto = UserDto.builder().id(user.getId()).name(newName).email(user.getEmail()).build();

        UserDto actualUserDto = userService.update(user.getId(), updUserDto);

        assertNotNull(actualUserDto);
        assertEquals(actualUserDto, expectedUserDto);
        assertEquals(actualUserDto.getId(), expectedUserDto.getId());
        assertEquals(actualUserDto.getName(), expectedUserDto.getName());
        assertEquals(actualUserDto.getEmail(), expectedUserDto.getEmail());
    }

    @Test
    void updateUser_shouldUpdateEmailUserDto_whenUserFound() {
        String newEmail = "new@email.com";
        UserDto updUserDto = UserDto.builder().email(newEmail).build();
        User updUser = User.builder().id(user.getId()).name(user.getName()).email(newEmail).build();
        when(userRepository.getUserById(user.getId())).thenReturn(user);
        when(userRepository.save(updUser)).thenReturn(updUser);
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.empty());
        UserDto expectedUserDto = UserDto.builder().id(user.getId()).name(user.getName()).email(newEmail).build();

        UserDto actualUserDto = userService.update(user.getId(), updUserDto);

        assertNotNull(actualUserDto);
        assertEquals(actualUserDto, expectedUserDto);
        assertEquals(actualUserDto.getId(), expectedUserDto.getId());
        assertEquals(actualUserDto.getName(), expectedUserDto.getName());
        assertEquals(actualUserDto.getEmail(), expectedUserDto.getEmail());
    }

    @Test
    void updateUser_shouldReturnNotFoundException_whenUserNotFound() {
        Long notFoundId = 2L;
        String newEmail = "new@email.com";
        UserDto updUserDto = UserDto.builder().email(newEmail).build();

        when(userRepository.getUserById(notFoundId)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> userService.update(notFoundId, updUserDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_shouldThrowException_whenUserWithSameEmailAlreadyExists() {
        String newUserName = "new user name";
        String newUserEmail = "email_that_exists@test.com";
        Long anotherId = 2L;
        String anotherName = "another user";
        String anotherEmail = "email_that_exists@test.com";
        User anotherUser = User.builder().id(anotherId).name(anotherName).email(anotherEmail).build();
        UserDto updateUserDto = UserDto.builder().id(user.getId()).name(newUserName).email(newUserEmail).build();
        when(userRepository.getUserById(user.getId())).thenReturn(user);

        when(userRepository.findUserByEmail(anotherEmail)).thenReturn(Optional.of(anotherUser));

        assertThrows(UniqueConstraintException.class, () -> userService.update(user.getId(), updateUserDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_shouldDelete_whenUserFound() {
        when(userRepository.getUserById(user.getId()))
                .thenReturn(user);

        userService.delete(user.getId());
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_shouldThrowNotFoundException_whenUserNotFound() {
        Long notFoundId = 2L;
        when(userRepository.getUserById(notFoundId)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> userService.delete(notFoundId));
        verify(userRepository, never()).delete(any(User.class));
    }
}

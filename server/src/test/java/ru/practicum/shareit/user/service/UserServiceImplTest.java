package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
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

    @Test
    void findUserById_shouldReturnUserDto_whenUserFound() {
        Long id = 1L;
        String name = "test user";
        String email = "test@test.com";
        when(userRepository.getUserById(id))
                .thenReturn(
                        User.builder().id(id).name(name).email(email).build());
        UserDto expectedUserDto = UserDto.builder().id(id).name(name).email(email).build();

        UserDto actualUserDto = userService.findUserById(id);

        assertNotNull(actualUserDto);
        assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    void findUserById_shouldReturnNotFoundException_whenUserNotFound() {
        Long id = 1L;

        when(userRepository.getUserById(id)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> userService.findUserById(id));
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void createUser_shouldCreateUserDto() {
        Long id = 1L;
        String name = "test user";
        String email = "test@test.com";
        User newUser = User.builder().id(id).name(name).email(email).build();
        UserDto newUserDto = UserDto.builder().id(id).name(name).email(email).build();
        when(userRepository.save(newUser)).thenReturn(newUser);
        UserDto expectedUserDto = UserDto.builder().id(id).name(name).email(email).build();

        UserDto actualUserDto = userService.create(newUserDto);

        assertNotNull(actualUserDto);
        assertEquals(expectedUserDto, actualUserDto);
        assertEquals(expectedUserDto.getName(), actualUserDto.getName());
        assertEquals(expectedUserDto.getEmail(), actualUserDto.getEmail());
    }

    @Test
    void createUser_shouldThrowException_whenUserWithSameEmailAlreadyExists() {
        Long id = 1L;
        String name = "test user";
        String email = "test@test.com";
        Long newUserId = 2L;
        String newUserName = "another test user";
        String newUserEmail = "test@test.com";
        User user = User.builder().id(id).name(name).email(email).build();
        UserDto newUserDto = UserDto.builder().id(newUserId).name(newUserName).email(newUserEmail).build();
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(UniqueConstraintException.class, () -> userService.create(newUserDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_shouldUpdateUserDto() {
        Long id = 1L;
        String name = "test user";
        String email = "test@test.com";
        String newName = "new name";
        String newEmail = "new@email.com";
        UserDto updUserDto = UserDto.builder().id(id).name(newName).email(newEmail).build();
        User updUser = User.builder().id(id).name(newName).email(newEmail).build();
        when(userRepository.getUserById(id))
                .thenReturn(
                        User.builder().id(id).name(name).email(email).build());
        when(userRepository.save(updUser)).thenReturn(updUser);
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());
        UserDto expectedUserDto = UserDto.builder().id(id).name(newName).email(newEmail).build();

        UserDto actualUserDto = userService.update(id, updUserDto);

        assertNotNull(actualUserDto);
        assertEquals(expectedUserDto, actualUserDto);
        assertEquals(expectedUserDto.getId(), actualUserDto.getId());
        assertEquals(expectedUserDto.getName(), actualUserDto.getName());
        assertEquals(expectedUserDto.getEmail(), actualUserDto.getEmail());
    }

    @Test
    void updateUser_shouldUpdateNameUserDto_whenUserFound() {
        Long id = 1L;
        String name = "test user";
        String email = "test@test.com";
        String newName = "new name";
        UserDto updUserDto = UserDto.builder().id(id).name(newName).build();
        User updUser = User.builder().id(id).name(newName).email(email).build();
        when(userRepository.getUserById(id))
                .thenReturn(
                        User.builder().id(id).name(name).email(email).build());
        when(userRepository.save(updUser)).thenReturn(updUser);
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());
        UserDto expectedUserDto = UserDto.builder().id(id).name(newName).email(email).build();

        UserDto actualUserDto = userService.update(id, updUserDto);

        assertNotNull(actualUserDto);
        assertEquals(actualUserDto, expectedUserDto);
        assertEquals(actualUserDto.getId(), expectedUserDto.getId());
        assertEquals(actualUserDto.getName(), expectedUserDto.getName());
        assertEquals(actualUserDto.getEmail(), expectedUserDto.getEmail());
    }

    @Test
    void updateUser_shouldUpdateEmailUserDto_whenUserFound() {
        Long id = 1L;
        String name = "test user";
        String email = "test@test.com";
        String newEmail = "new@email.com";
        UserDto updUserDto = UserDto.builder().id(id).email(newEmail).build();
        User updUser = User.builder().id(id).name(name).email(newEmail).build();
        when(userRepository.getUserById(id))
                .thenReturn(
                        User.builder().id(id).name(name).email(email).build());
        when(userRepository.save(updUser)).thenReturn(updUser);
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());
        UserDto expectedUserDto = UserDto.builder().id(id).name(name).email(newEmail).build();

        UserDto actualUserDto = userService.update(id, updUserDto);

        assertNotNull(actualUserDto);
        assertEquals(actualUserDto, expectedUserDto);
        assertEquals(actualUserDto.getId(), expectedUserDto.getId());
        assertEquals(actualUserDto.getName(), expectedUserDto.getName());
        assertEquals(actualUserDto.getEmail(), expectedUserDto.getEmail());
    }

    @Test
    void updateUser_shouldReturnNotFoundException_whenUserNotFound() {
        Long id = 1L;
        Long notFoundId = 2L;
        String newEmail = "new@email.com";
        UserDto updUserDto = UserDto.builder().id(id).email(newEmail).build();

        when(userRepository.getUserById(notFoundId)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> userService.update(notFoundId, updUserDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_shouldThrowException_whenUserWithSameEmailAlreadyExists() {
        Long id = 1L;
        String name = "test user";
        String email = "test@test.com";
        String newUserName = "new user name";
        String newUserEmail = "email_that_exists@test.com";
        Long anotherId = 2L;
        String anotherName = "another user";
        String anotherEmail = "email_that_exists@test.com";
        User anotherUser = User.builder().id(anotherId).name(anotherName).email(anotherEmail).build();
        UserDto updateUserDto = UserDto.builder().id(id).name(newUserName).email(newUserEmail).build();
        when(userRepository.getUserById(id))
                .thenReturn(
                        User.builder().id(id).name(name).email(email).build());
        when(userRepository.findUserByEmail(anotherEmail)).thenReturn(Optional.of(anotherUser));

        assertThrows(UniqueConstraintException.class, () -> userService.update(id, updateUserDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_shouldDelete_whenUserFound() {
        Long id = 1L;
        String name = "test user";
        String email = "test@test.com";
        User user = User.builder().id(id).name(name).email(email).build();
        when(userRepository.getUserById(id))
                .thenReturn(user);

        userService.delete(id);
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

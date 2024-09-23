package ru.practicum.shareit.user.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class UserServiceIntegrationTest {

    private final UserService userService;

    private final EntityManager em;

    @Test
    void createUser_fullIntegrationTest() {
        String userName = "test user";
        String userEmail = "test@test.com";
        UserDto userToSaveDto = UserDto.builder()
                .name(userName)
                .email(userEmail)
                .build();

        UserDto actualUserDto = userService.create(userToSaveDto);

        assertThat(actualUserDto, notNullValue());
        assertThat(actualUserDto, allOf(
                hasProperty("id", notNullValue()),
                hasProperty("name", equalTo(userName)),
                hasProperty("email", equalTo(userEmail))
        ));
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User actualUser = query.setParameter("email", userToSaveDto.getEmail())
                .getSingleResult();
        assertThat(actualUser, notNullValue());
        assertThat(actualUser, allOf(
                hasProperty("id", equalTo(actualUserDto.getId())),
                hasProperty("name", equalTo(actualUserDto.getName())),
                hasProperty("email", equalTo(actualUserDto.getEmail()))
        ));
    }

    @Test
    void findUserById_shouldThrowNotFoundException_whenUserDoesNotExist() {
        Assertions.assertThrows(NotFoundException.class, () -> userService.findUserById(0L));
    }
}
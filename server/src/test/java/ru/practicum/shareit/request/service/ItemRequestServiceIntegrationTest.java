package ru.practicum.shareit.request.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class ItemRequestServiceIntegrationTest {

    private final ItemRequestService itemRequestService;

    private final EntityManager em;

    User requestor;

    @BeforeEach
    void setUp() {
        String requestorUserName = "test user requestor";
        String requestorUserEmail = "requestor@test.com";
        requestor = User.builder()
                .name(requestorUserName).email(requestorUserEmail).build();
        em.persist(requestor);
    }

    @Test
    void create_fullIntegrationTest() {
        String description = "request description";
        ItemRequestShortDto itemRequestShortDto = new ItemRequestShortDto(description);

        ItemRequestDto actualItemRequestDto = itemRequestService.create(requestor.getId(), itemRequestShortDto);
        ItemRequestFullDto actualItemRequestFullDto = itemRequestService.getItemRequestById(actualItemRequestDto.getId()) ;

        assertThat(actualItemRequestDto, notNullValue());
        assertThat(actualItemRequestDto, allOf(
                hasProperty("id", notNullValue()),
                hasProperty("description", equalTo(itemRequestShortDto.getDescription())),
                hasProperty("created", notNullValue(LocalDateTime.class))
        ));
        assertThat(actualItemRequestFullDto, notNullValue());
        assertThat(actualItemRequestFullDto, allOf(
                hasProperty("id", equalTo(actualItemRequestDto.getId())),
                hasProperty("description", equalTo(actualItemRequestDto.getDescription())),
                hasProperty("created", equalTo(actualItemRequestDto.getCreated()))
        ));
    }

    @Test
    void getItemRequestById_shouldThrowNotFoundException_whenItemRequestDoesNotExist() {
        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(0L));
    }
}
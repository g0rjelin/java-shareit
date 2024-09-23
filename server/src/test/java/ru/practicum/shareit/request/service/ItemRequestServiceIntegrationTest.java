package ru.practicum.shareit.request.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;

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

    @Test
    void create_fullIntegrationTest() {
        String requestorName = "test user";
        String requestorEmail = "test@test.com";
        Query query = em.createNativeQuery("insert into users(name, email) values (:requestorName, :requestorEmail)");
        query.setParameter("requestorName", requestorName);
        query.setParameter("requestorEmail", requestorEmail);
        query.executeUpdate();
        TypedQuery<Long> getIdQuery = em.createQuery("select id from User where  email = :email", Long.class);
        getIdQuery.setParameter("email", requestorEmail);
        Long requestorId = getIdQuery.getSingleResult();
        String description = "request description";
        ItemRequestShortDto itemRequestShortDto = new ItemRequestShortDto(description);

        ItemRequestDto actualItemRequestDto = itemRequestService.create(requestorId, itemRequestShortDto);

        assertThat(actualItemRequestDto, notNullValue());
        assertThat(actualItemRequestDto, allOf(
                hasProperty("id", notNullValue()),
                hasProperty("description", equalTo(itemRequestShortDto.getDescription())),
                hasProperty("created", notNullValue(LocalDateTime.class))
        ));
        TypedQuery<ItemRequest> irQuery = em.createQuery("Select ir from ItemRequest ir where ir.description = :description", ItemRequest.class);
        ItemRequest actualItemRequest = irQuery.setParameter("description", description)
                .getSingleResult();
        assertThat(actualItemRequest, notNullValue());
        assertThat(actualItemRequest, allOf(
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
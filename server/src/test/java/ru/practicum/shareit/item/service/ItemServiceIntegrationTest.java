package ru.practicum.shareit.item.service;


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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class ItemServiceIntegrationTest {

    private final ItemService itemService;

    private final EntityManager em;

    User owner;
    Item item;
    ItemShortDto itemShortDto;

    @BeforeEach
    void setUp() {
        String ownerUserName = "test user owner";
        String ownerUserEmail = "owner@test.com";
        owner = User.builder()
                .name(ownerUserName).email(ownerUserEmail).build();
        em.persist(owner);

        String itemName = "test item";
        String itemDescription = "test item description";
        Boolean isAvailable = true;
        item = Item.builder()
                .name(itemName).description(itemDescription).available(isAvailable).owner(owner).build();

        itemShortDto = ItemShortDto.builder().name(itemName).description(itemDescription).available(isAvailable).build();
    }

    @Test
    void getUserItems_fullIntegrationTest() {
        ItemDto expectedItemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .comments(new ArrayList<>())
                .available(item.getAvailable())
                .build();
        Collection<ItemDto> expectedItemDtos = List.of(expectedItemDto);

        itemService.create(owner.getId(), itemShortDto);
        Collection<ItemDto> actualItemDtos = itemService.findAllItemsByOwnerId(owner.getId());

        assertThat(actualItemDtos, hasSize(expectedItemDtos.size()));
        for (ItemDto actualItemDto : actualItemDtos) {
            assertThat(expectedItemDtos, hasItem(allOf(
                    hasProperty("id"),
                    hasProperty("name", equalTo(actualItemDto.getName())),
                    hasProperty("description", equalTo(actualItemDto.getDescription())),
                    hasProperty("available", equalTo(actualItemDto.getAvailable())),
                    hasProperty("request", nullValue())
            )));
        }
    }

    @Test
    void findItemById_shouldThrowNotFoundException_whenUserItemNotExist() {
        Assertions.assertThrows(NotFoundException.class, () -> itemService.findItemById(0L));
    }
}

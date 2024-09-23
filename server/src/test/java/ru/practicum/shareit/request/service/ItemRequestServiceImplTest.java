package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {

    @Autowired
    private final ItemRequestService itemRequestService;

    @MockBean
    private ItemRequestRepository itemRequestRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRepository itemRepository;


    @Test
    void createItemRequest_shouldCreateItemRequest_whenUserFound() {
        String description = "test description";
        ItemRequestShortDto itemRequestShortDto = new ItemRequestShortDto(description);
        Long id = 1L;
        String name = "test user";
        String email = "test@test.com";
        User requestor = User.builder().id(id).name(name).email(email).build();
        Long itemRequestId = 100L;
        LocalDateTime created = LocalDateTime.now();
        when(userRepository.getUserById(id)).thenReturn(requestor);
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(ItemRequest.builder().id(itemRequestId).description(description).requestor(requestor).created(created).build());
        ItemRequestDto expectedItemRequestDto = ItemRequestDto.builder()
                .id(itemRequestId)
                .description(description)
                .created(created)
                .build();

        ItemRequestDto actualItemRequestDto = itemRequestService.create(id, itemRequestShortDto);

        assertNotNull(actualItemRequestDto);
        assertEquals(expectedItemRequestDto, actualItemRequestDto);
        assertEquals(expectedItemRequestDto.getDescription(), actualItemRequestDto.getDescription());
        assertEquals(expectedItemRequestDto.getCreated(), actualItemRequestDto.getCreated());
    }

    @Test
    void createItemRequest_shouldThrowException_whenUserNotFound() {
        Long notFoundId = 2L;
        String description = "test description";
        ItemRequestShortDto itemRequestShortDto = new ItemRequestShortDto(description);
        when(userRepository.getUserById(notFoundId)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.create(notFoundId, itemRequestShortDto));
        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void getItemRequestsByRequestorId_shouldReturnItemRequestFullDtoCollection_whenUserFound() {
        Long requestorId = 1L;
        String name = "test user";
        String email = "test@test.com";
        User requestor = User.builder().id(requestorId).name(name).email(email).build();
        Long ownerId = 1L;
        String ownerName = "test owner user";
        String ownerEmail = "owner@test.com";
        User owner = User.builder().id(ownerId).name(ownerName).email(ownerEmail).build();
        Long itemRequestId = 100L;
        String description = "test description";
        LocalDateTime created = LocalDateTime.now();
        when(userRepository.getUserById(requestorId)).thenReturn(requestor);
        ItemRequest itemRequest = ItemRequest.builder()
                .id(itemRequestId)
                .description(description)
                .created(created)
                .requestor(requestor)
                .build();
        List<ItemRequest> itemRequests = List.of(itemRequest);
        Page<ItemRequest> pagedItemRequest = new PageImpl<ItemRequest>(itemRequests);
        PageRequest page = PageRequest.of(0, 10);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestorId, page))
                .thenReturn(pagedItemRequest);
        Long itemId = 10L;
        String itemName = "test item";
        String itemDescription = "test description";
        List<Item> items = List.of(
                Item.builder()
                        .id(itemId)
                        .name(itemName)
                        .description(itemDescription)
                        .owner(owner)
                        .available(true)
                        .request(itemRequest)
                        .build());
        when(itemRepository.findAllByRequest_RequestorId(requestorId))
                .thenReturn(items);
        Collection<ItemRequestFullDto> expectedItemRequestFullDtos = List.of(
                ItemRequestFullDto.builder()
                        .id(itemRequestId)
                        .description(description)
                        .created(created)
                        .items(List.of(
                                ItemForItemRequestDto.builder().id(itemId).name(itemName).ownerId(ownerId).build()
                        ))
                        .build()
        );

        Collection<ItemRequestFullDto> actualItemRequestFullDtos = itemRequestService.getItemRequestsByRequestorId(requestorId, 0, 10);

        assertNotNull(actualItemRequestFullDtos);
        Assertions.assertIterableEquals(expectedItemRequestFullDtos, actualItemRequestFullDtos);
    }

    @Test
    void getItemRequestsByRequestorId_shouldThrowException_whenUserNotFound() {
        Long notFoundId = 2L;
        when(userRepository.getUserById(notFoundId)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestsByRequestorId(notFoundId, 0, 10));
    }

    @Test
    void getAllOtherItemRequestsByUserId_shouldReturnItemRequestDtoCollection_whenUserFound() {
        Long userId = 1L;
        Long requestorId = 2L;
        String name = "test user";
        String email = "test@test.com";
        User requestor = User.builder().id(requestorId).name(name).email(email).build();
        Long itemRequestId = 100L;
        String description = "test description";
        LocalDateTime created = LocalDateTime.now();
        when(userRepository.getUserById(requestorId)).thenReturn(requestor);
        ItemRequest itemRequest = ItemRequest.builder()
                .id(itemRequestId)
                .description(description)
                .created(created)
                .requestor(requestor)
                .build();
        List<ItemRequest> itemRequests = List.of(itemRequest);
        Page<ItemRequest> pagedItemRequest = new PageImpl<ItemRequest>(itemRequests);
        PageRequest page = PageRequest.of(0, 10);
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId, page))
                .thenReturn(pagedItemRequest);
        Collection<ItemRequestDto> expectedItemRequestDtos = List.of(
                ItemRequestDto.builder()
                        .id(itemRequestId)
                        .description(description)
                        .created(created)
                        .build()
        );

        Collection<ItemRequestDto> actualItemRequestDtos = itemRequestService.getAllOtherItemRequestsByUserId(userId, 0, 10);

        assertNotNull(actualItemRequestDtos);
        Assertions.assertIterableEquals(expectedItemRequestDtos, actualItemRequestDtos);

    }

    @Test
    void getAllOtherItemRequestsByUserId_shouldThrowException_whenUserNotFound() {
        Long notFoundId = 2L;
        when(userRepository.getUserById(notFoundId)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.getAllOtherItemRequestsByUserId(notFoundId, 0, 10));
    }

    @Test
    void getItemRequestById_shouldReturnItemRequestFullDto_whenItemRequestFound() {
        Long requestorId = 1L;
        String name = "test user";
        String email = "test@test.com";
        User requestor = User.builder().id(requestorId).name(name).email(email).build();
        Long ownerId = 1L;
        String ownerName = "test owner user";
        String ownerEmail = "owner@test.com";
        User owner = User.builder().id(ownerId).name(ownerName).email(ownerEmail).build();
        Long itemRequestId = 100L;
        String description = "test description";
        LocalDateTime created = LocalDateTime.now();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(itemRequestId)
                .description(description)
                .created(created)
                .requestor(requestor)
                .build();
        when(itemRequestRepository.getItemRequestBy(itemRequestId)).thenReturn(itemRequest);
        Long itemId = 10L;
        String itemName = "test item";
        String itemDescription = "test description";
        List<Item> items = List.of(
                Item.builder()
                        .id(itemId)
                        .name(itemName)
                        .description(itemDescription)
                        .owner(owner)
                        .available(true)
                        .request(itemRequest)
                        .build());
        ItemRequestFullDto expectedItemRequestFullDto =
                ItemRequestFullDto.builder()
                        .id(itemRequestId)
                        .description(description)
                        .created(created)
                        .items(List.of(
                                ItemForItemRequestDto.builder().id(itemId).name(itemName).ownerId(ownerId).build()
                        ))
                        .build();
        when(itemRepository.findAllByRequestId(itemRequestId)).thenReturn(items);

        ItemRequestFullDto actualItemRequestFullDto = itemRequestService.getItemRequestById(itemRequestId);

        assertNotNull(actualItemRequestFullDto);
        assertEquals(expectedItemRequestFullDto, actualItemRequestFullDto);
        assertIterableEquals(expectedItemRequestFullDto.getItems(), actualItemRequestFullDto.getItems());
        assertEquals(expectedItemRequestFullDto.getDescription(), actualItemRequestFullDto.getDescription());
        assertEquals(expectedItemRequestFullDto.getCreated(), actualItemRequestFullDto.getCreated());
    }

    @Test
    void getItemRequestById_shouldThrowException_whenItemRequestNotFound() {
        Long itemRequestId = 1L;
        when(itemRequestRepository.getItemRequestBy(itemRequestId)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(itemRequestId));
    }
}
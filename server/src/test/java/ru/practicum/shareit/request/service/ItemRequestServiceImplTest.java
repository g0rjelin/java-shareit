package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

    User user;
    User requestor;
    User owner;
    Item item;
    List<Item> items;
    ItemRequest itemRequest;
    List<ItemRequest> itemRequests;
    ItemRequestDto itemRequestDto;
    ItemRequestShortDto itemRequestShortDto;
    ItemRequestFullDto itemRequestFullDto;
    Collection<ItemRequestDto> itemRequestDtos;
    Collection<ItemRequestFullDto> itemRequestFullDtos;

    @BeforeEach
    void ItemRequestServiceTestSetUp() {
        Long id = 1L;
        String name = "test user";
        String email = "test@test.com";
        user = User.builder().id(id).name(name).email(email).build();
        Long requestorId = 2L;
        String requestorName = "test requestor";
        String requestorEmail = "requestor@test.com";
        requestor = User.builder().id(requestorId).name(requestorName).email(requestorEmail).build();
        Long ownerId = 3L;
        String ownerName = "test owner user";
        String ownerEmail = "owner@test.com";
        owner = User.builder().id(ownerId).name(ownerName).email(ownerEmail).build();
        Long itemRequestId = 100L;
        String itemRequestDescription = "test description";
        LocalDateTime created = LocalDateTime.now();
        itemRequest = ItemRequest.builder().id(itemRequestId).description(itemRequestDescription).requestor(requestor).created(created).build();
        itemRequests = List.of(itemRequest);
        Long itemId = 10L;
        String itemName = "test item";
        String itemDescription = "test description";
        item = Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .owner(owner)
                .available(true)
                .request(itemRequest)
                .build();
        items = List.of(item);

        itemRequestDto = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
        itemRequestDtos = List.of(itemRequestDto);
        itemRequestFullDto = ItemRequestFullDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(List.of(
                        ItemForItemRequestDto.builder().id(item.getId()).name(item.getName()).ownerId(owner.getId()).build()
                ))
                .build();
        itemRequestFullDtos = List.of(itemRequestFullDto);
        itemRequestShortDto = new ItemRequestShortDto(itemRequest.getDescription());
    }

    @Test
    void createItemRequest_shouldCreateItemRequest_whenUserFound() {
        when(userRepository.getUserById(itemRequest.getId())).thenReturn(requestor);
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);
        ItemRequestDto expectedItemRequestDto = itemRequestDto;

        ItemRequestDto actualItemRequestDto = itemRequestService.create(itemRequest.getId(), itemRequestShortDto);

        assertNotNull(actualItemRequestDto);
        assertEquals(expectedItemRequestDto, actualItemRequestDto);
        assertEquals(expectedItemRequestDto.getDescription(), actualItemRequestDto.getDescription());
        assertEquals(expectedItemRequestDto.getCreated(), actualItemRequestDto.getCreated());
    }

    @Test
    void createItemRequest_shouldThrowException_whenUserNotFound() {
        when(userRepository.getUserById(user.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.create(user.getId(), itemRequestShortDto));
        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void getItemRequestsByRequestorId_shouldReturnItemRequestFullDtoCollection_whenUserFound() {
        when(userRepository.getUserById(requestor.getId())).thenReturn(requestor);
        Page<ItemRequest> pagedItemRequest = new PageImpl<ItemRequest>(itemRequests);
        PageRequest page = PageRequest.of(0, 10);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestor.getId(), page))
                .thenReturn(pagedItemRequest);
        when(itemRepository.findAllByRequest_RequestorId(requestor.getId()))
                .thenReturn(items);
        Collection<ItemRequestFullDto> expectedItemRequestFullDtos = itemRequestFullDtos;

        Collection<ItemRequestFullDto> actualItemRequestFullDtos = itemRequestService.getItemRequestsByRequestorId(requestor.getId(), 0, 10);

        assertNotNull(actualItemRequestFullDtos);
        Assertions.assertIterableEquals(expectedItemRequestFullDtos, actualItemRequestFullDtos);
    }

    @Test
    void getItemRequestsByRequestorId_shouldThrowException_whenUserNotFound() {
        when(userRepository.getUserById(user.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestsByRequestorId(user.getId(), 0, 10));
    }

    @Test
    void getAllOtherItemRequestsByUserId_shouldReturnItemRequestDtoCollection_whenUserFound() {
        when(userRepository.getUserById(user.getId())).thenReturn(user);
        Page<ItemRequest> pagedItemRequest = new PageImpl<ItemRequest>(itemRequests);
        PageRequest page = PageRequest.of(0, 10);
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(user.getId(), page))
                .thenReturn(pagedItemRequest);
        Collection<ItemRequestDto> expectedItemRequestDtos = itemRequestDtos;

        Collection<ItemRequestDto> actualItemRequestDtos = itemRequestService.getAllOtherItemRequestsByUserId(user.getId(), 0, 10);

        assertNotNull(actualItemRequestDtos);
        Assertions.assertIterableEquals(expectedItemRequestDtos, actualItemRequestDtos);
    }

    @Test
    void getAllOtherItemRequestsByUserId_shouldThrowException_whenUserNotFound() {
        when(userRepository.getUserById(user.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.getAllOtherItemRequestsByUserId(user.getId(), 0, 10));
    }

    @Test
    void getItemRequestById_shouldReturnItemRequestFullDto_whenItemRequestFound() {
        when(itemRequestRepository.getItemRequestBy(itemRequest.getId())).thenReturn(itemRequest);
        ItemRequestFullDto expectedItemRequestFullDto = itemRequestFullDto;
        when(itemRepository.findAllByRequestId(itemRequest.getId())).thenReturn(items);

        ItemRequestFullDto actualItemRequestFullDto = itemRequestService.getItemRequestById(itemRequest.getId());

        assertNotNull(actualItemRequestFullDto);
        assertEquals(expectedItemRequestFullDto, actualItemRequestFullDto);
        assertIterableEquals(expectedItemRequestFullDto.getItems(), actualItemRequestFullDto.getItems());
        assertEquals(expectedItemRequestFullDto.getDescription(), actualItemRequestFullDto.getDescription());
        assertEquals(expectedItemRequestFullDto.getCreated(), actualItemRequestFullDto.getCreated());
    }

    @Test
    void getItemRequestById_shouldThrowException_whenItemRequestNotFound() {
        when(itemRequestRepository.getItemRequestBy(itemRequest.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(itemRequest.getId()));
    }
}
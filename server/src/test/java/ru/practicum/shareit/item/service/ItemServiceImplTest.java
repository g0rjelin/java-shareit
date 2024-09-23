package ru.practicum.shareit.item.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    @Autowired
    private final ItemService itemService;

    @MockBean
    private ItemRequestRepository itemRequestRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private CommentRepository commentRepository;

    @Test
    void findAllItemsByOwnerId_shouldReturnCollectionItemDto_whenUserFound() {
        Long ownerId = 1L;
        String ownerName = "test owner user";
        String ownerEmail = "owner@test.com";
        User owner = User.builder().id(ownerId).name(ownerName).email(ownerEmail).build();
        when(userRepository.getUserById(ownerId)).thenReturn(owner);
        Long authorId = 2L;
        String authorName = "test author user";
        String authorEmail = "author@test.com";
        User author = User.builder().id(authorId).name(authorName).email(authorEmail).build();
        when(userRepository.getUserById(authorId)).thenReturn(author);
        Long itemId = 10L;
        String itemName = "test item name";
        String itemDescription = "test item description";
        Boolean available = true;
        Item item = Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(available)
                .owner(owner)
                .build();
        List<Item> items = List.of(item);
        when(itemRepository.findAllByOwnerId(ownerId)).thenReturn(items);
        LocalDateTime prevBookingStartDate = LocalDateTime.of(2024, 1, 1, 12, 30);
        LocalDateTime prevBookingEndDate = LocalDateTime.of(2024, 1, 1, 18, 30);
        LocalDateTime nextBookingStartDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)).plusDays(1);
        LocalDateTime nextBookingEndDate = nextBookingStartDate.plusHours(10);
        Long prevBookingId = 1000L;
        Long nextBookingId = 2000L;
        Booking prevBooking = Booking.builder()
                .id(prevBookingId)
                .status(BookingStatus.APPROVED)
                .start(prevBookingStartDate)
                .end(prevBookingEndDate)
                .item(item)
                .build();
        Booking nextBooking = Booking.builder()
                .id(nextBookingId)
                .status(BookingStatus.APPROVED)
                .start(nextBookingStartDate)
                .end(nextBookingEndDate)
                .item(item)
                .build();
        List<Booking> bookings = List.of(prevBooking, nextBooking);
        when(bookingRepository.findByItem_Owner_Id(ownerId)).thenReturn(bookings);
        Long commentId = 50L;
        String commentText = "test comment";
        LocalDateTime created = LocalDateTime.now();
        Comment comment = Comment.builder()
                .id(commentId)
                .text(commentText)
                .author(author)
                .item(item)
                .created(created)
                .build();
        List<Comment> comments = List.of(comment);
        when(commentRepository.findByItem_Owner_Id(ownerId)).thenReturn(comments);
        List<ItemDto> expectedItemDtos = List.of(
                ItemDto.builder()
                        .id(itemId)
                        .name(itemName)
                        .description(itemDescription)
                        .available(available)
                        .request(null)
                        .lastBooking(prevBookingEndDate)
                        .nextBooking(nextBookingStartDate)
                        .comments(List.of(
                                CommentDto.builder()
                                        .id(commentId)
                                        .text(commentText)
                                        .authorName(authorName)
                                        .created(created)
                                        .build()
                        ))
                        .build());

        Collection<ItemDto> actualItemDtos = itemService.findAllItemsByOwnerId(ownerId);

        assertEquals(expectedItemDtos.size(), actualItemDtos.size());
        assertIterableEquals(expectedItemDtos, actualItemDtos);
    }

    @Test
    void findAllItemsByOwnerId_shouldThrowNotFoundException_whenUserNotFound() {
        Long notFoundOwnerId = 1L;
        when(userRepository.getUserById(notFoundOwnerId)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.findAllItemsByOwnerId(notFoundOwnerId));
        verify(itemRepository, never()).findAllByOwnerId(anyLong());
        verify(commentRepository, never()).findByItem_Owner_Id(anyLong());
        verify(bookingRepository, never()).findByItem_Owner_Id(anyLong());
    }

    @Test
    void findItemById_shouldReturnItemDto_whenItemFound() {
        Long itemId = 10L;
        String itemName = "test item name";
        String itemDescription = "test item description";
        Boolean available = true;
        Long ownerId = 1L;
        User owner = User.builder().id(ownerId).name("owner").email("owner@test.com").build();
        Item item = Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(available)
                .owner(owner)
                .build();
        when(itemRepository.getItemById(itemId)).thenReturn(item);
        Long commentId = 50L;
        String commentText = "test comment";
        LocalDateTime created = LocalDateTime.now();
        String authorName = "author name";
        Comment comment = Comment.builder()
                .id(commentId)
                .text(commentText)
                .author(User.builder().id(2L).name(authorName).email("email@test.com").build())
                .item(item)
                .created(created)
                .build();
        List<Comment> comments = List.of(comment);
        when(commentRepository.findAllByItemId(itemId)).thenReturn(comments);
        ItemDto expectedItemDto = ItemDto.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(available)
                .comments(List.of(CommentDto.builder()
                        .id(commentId)
                        .text(commentText)
                        .created(created)
                        .authorName(authorName)
                        .build()))
                .build();

        ItemDto actualItemDto = itemService.findItemById(itemId);

        assertNotNull(actualItemDto);
        assertEquals(expectedItemDto, actualItemDto);
        assertEquals(itemId, actualItemDto.getId());
        assertEquals(itemName, actualItemDto.getName());
        assertEquals(itemDescription, actualItemDto.getDescription());
        assertEquals(available, actualItemDto.getAvailable());
        assertIterableEquals(expectedItemDto.getComments(), actualItemDto.getComments());
    }

    @Test
    void findItemById_shouldThrowNotFoundException_whenItemNotFound() {
        Long itemId = 10L;
        when(itemRepository.getItemById(itemId)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.findItemById(itemId));
        verify(commentRepository, never()).findAllByItemId(anyLong());
    }

    @Test
    void searchItems_shouldReturnEmptyCollection_whenTextStringIsEmpty() {
        String text = "";

        Collection<ItemShortDto> itemDtos = itemService.searchItems(text);

        assertEquals(0, itemDtos.size());
        verify(itemRepository, never()).findAll(any(BooleanExpression.class));
    }

    @Test
    void searchItems_shouldReturnCollectionItemShort_whenTextStringIsNotEmpty() {
        String text = "some text";
        List<ItemShortDto> expectedItemShortDtos = List.of(
                ItemShortDto.builder()
                        .id(1L)
                        .name("name")
                        .description("description")
                        .available(true)
                        .requestId(null)
                        .build()
        );
        when(itemRepository.findAll(any(BooleanExpression.class))).thenReturn(
                List.of(Item.builder().id(1L).name("name").description("description").available(true).build())
        );

        Collection<ItemShortDto> actualItemDtos = itemService.searchItems(text);

        assertEquals(expectedItemShortDtos.size(), actualItemDtos.size());
        assertIterableEquals(expectedItemShortDtos, actualItemDtos);
        verify(itemRepository, times(1)).findAll(any(BooleanExpression.class));
    }

    @Test
    void createItem_shouldCreateItem_whenUserFoundAndNoRequestOrRequestExists() {
        String itemName = "test item name";
        String itemDescription = "test item description";
        Boolean available = Boolean.TRUE;
        Long requestId = 100L;
        ItemShortDto itemToSaveShortDto = ItemShortDto.builder()
                .name(itemName)
                .description(itemDescription)
                .available(available)
                .requestId(requestId)
                .build();
        Long requestorId = 3L;
        String requestorName = "test requestor user";
        String requestorEmail = "requestor@test.com";
        User requestor = User.builder().id(requestorId).name(requestorName).email(requestorEmail).build();
        Long ownerId = 1L;
        String ownerName = "test owner user";
        String ownerEmail = "owner@test.com";
        User owner = User.builder().id(ownerId).name(ownerName).email(ownerEmail).build();
        when(userRepository.getUserById(ownerId)).thenReturn(owner);
        String requestDescription = "test description";
        LocalDateTime created = LocalDateTime.now();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(requestId)
                .description(requestDescription)
                .created(created)
                .requestor(requestor)
                .build();
        Long itemId = 10L;
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(available)
                .request(itemRequest)
                .owner(owner)
                .build());
        ItemShortDto expectedItemShortDto = ItemShortDto.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(available)
                .requestId(requestId)
                .build();

        ItemShortDto actualItemShortDto = itemService.create(ownerId, itemToSaveShortDto);

        assertNotNull(actualItemShortDto);
        assertEquals(expectedItemShortDto, actualItemShortDto);
        assertEquals(expectedItemShortDto.getId(), actualItemShortDto.getId());
        assertEquals(expectedItemShortDto.getName(), actualItemShortDto.getName());
        assertEquals(expectedItemShortDto.getDescription(), actualItemShortDto.getDescription());
        assertEquals(expectedItemShortDto.getAvailable(), actualItemShortDto.getAvailable());
        assertEquals(expectedItemShortDto.getRequestId(), actualItemShortDto.getRequestId());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void createItem_shouldThrowNotFoundException_whenUserNotFound() {
        Long notFoundId = 1L;
        String name = "test item name";
        String description = "test item description";
        Boolean available = Boolean.TRUE;
        Long requestId = 2L;
        ItemShortDto itemShortDto = ItemShortDto.builder()
                .name(name)
                .description(description)
                .available(available)
                .requestId(requestId)
                .build();
        when(userRepository.getUserById(notFoundId)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.create(notFoundId, itemShortDto));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void createItem_shouldThrowNotFoundException_whenRequestNotFound() {
        Long ownerId = 1L;
        String name = "test item name";
        String description = "test item description";
        Boolean available = Boolean.TRUE;
        Long requestId = 2L;
        ItemShortDto itemShortDto = ItemShortDto.builder()
                .name(name)
                .description(description)
                .available(available)
                .requestId(requestId)
                .build();
        when(itemRequestRepository.getItemRequestBy(requestId)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.create(ownerId, itemShortDto));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_shouldUpdateItemName_whenUserFoundAndItemFound() {
        String itemName = "test item name";
        String newItemName = "new item name";
        String itemDescription = "test item description";
        Boolean available = Boolean.TRUE;
        ItemShortDto itemToUpdateShortDto = ItemShortDto.builder()
                .name(newItemName)
                .build();
        Long ownerId = 1L;
        String ownerName = "test owner user";
        String ownerEmail = "owner@test.com";
        User owner = User.builder().id(ownerId).name(ownerName).email(ownerEmail).build();
        when(userRepository.getUserById(ownerId)).thenReturn(owner);
        Long itemId = 10L;
        Item item = Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(available)
                .owner(owner)
                .build();
        when(itemRepository.getItemById(itemId)).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(Item.builder()
                .id(itemId)
                .name(newItemName)
                .description(itemDescription)
                .available(available)
                .owner(owner)
                .build());
        ItemShortDto expectedItemShortDto = ItemShortDto.builder()
                .id(itemId)
                .name(newItemName)
                .description(itemDescription)
                .available(available)
                .build();

        ItemShortDto actualItemShortDto = itemService.update(ownerId, itemId, itemToUpdateShortDto);

        assertNotNull(actualItemShortDto);
        assertEquals(expectedItemShortDto, actualItemShortDto);
        assertEquals(expectedItemShortDto.getId(), actualItemShortDto.getId());
        assertEquals(expectedItemShortDto.getName(), actualItemShortDto.getName());
        assertEquals(expectedItemShortDto.getDescription(), actualItemShortDto.getDescription());
        assertEquals(expectedItemShortDto.getAvailable(), actualItemShortDto.getAvailable());
        assertEquals(expectedItemShortDto.getRequestId(), actualItemShortDto.getRequestId());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateItem_shouldUpdateItemDescription_whenUserFoundAndItemFound() {
        String itemName = "test item name";
        String itemDescription = "test item description";
        String newItemDescription = "new item description";
        Boolean available = Boolean.TRUE;
        ItemShortDto itemToUpdateShortDto = ItemShortDto.builder()
                .description(newItemDescription)
                .build();
        Long ownerId = 1L;
        String ownerName = "test owner user";
        String ownerEmail = "owner@test.com";
        User owner = User.builder().id(ownerId).name(ownerName).email(ownerEmail).build();
        when(userRepository.getUserById(ownerId)).thenReturn(owner);
        Long itemId = 10L;
        Item item = Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(available)
                .owner(owner)
                .build();
        when(itemRepository.getItemById(itemId)).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(Item.builder()
                .id(itemId)
                .name(itemName)
                .description(newItemDescription)
                .available(available)
                .owner(owner)
                .build());
        ItemShortDto expectedItemShortDto = ItemShortDto.builder()
                .id(itemId)
                .name(itemName)
                .description(newItemDescription)
                .available(available)
                .build();

        ItemShortDto actualItemShortDto = itemService.update(ownerId, itemId, itemToUpdateShortDto);

        assertNotNull(actualItemShortDto);
        assertEquals(expectedItemShortDto, actualItemShortDto);
        assertEquals(expectedItemShortDto.getId(), actualItemShortDto.getId());
        assertEquals(expectedItemShortDto.getName(), actualItemShortDto.getName());
        assertEquals(expectedItemShortDto.getDescription(), actualItemShortDto.getDescription());
        assertEquals(expectedItemShortDto.getAvailable(), actualItemShortDto.getAvailable());
        assertEquals(expectedItemShortDto.getRequestId(), actualItemShortDto.getRequestId());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateItem_shouldUpdateItemAvailable_whenUserFoundAndItemFound() {
        String itemName = "test item name";
        String itemDescription = "test item description";
        Boolean available = Boolean.TRUE;
        Boolean newItemAvailable = !available;
        ItemShortDto itemToUpdateShortDto = ItemShortDto.builder()
                .available(newItemAvailable)
                .build();
        Long ownerId = 1L;
        String ownerName = "test owner user";
        String ownerEmail = "owner@test.com";
        User owner = User.builder().id(ownerId).name(ownerName).email(ownerEmail).build();
        when(userRepository.getUserById(ownerId)).thenReturn(owner);
        Long itemId = 10L;
        Item item = Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(available)
                .owner(owner)
                .build();
        when(itemRepository.getItemById(itemId)).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(newItemAvailable)
                .owner(owner)
                .build());
        ItemShortDto expectedItemShortDto = ItemShortDto.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(newItemAvailable)
                .build();

        ItemShortDto actualItemShortDto = itemService.update(ownerId, itemId, itemToUpdateShortDto);

        assertNotNull(actualItemShortDto);
        assertEquals(expectedItemShortDto, actualItemShortDto);
        assertEquals(expectedItemShortDto.getId(), actualItemShortDto.getId());
        assertEquals(expectedItemShortDto.getName(), actualItemShortDto.getName());
        assertEquals(expectedItemShortDto.getDescription(), actualItemShortDto.getDescription());
        assertEquals(expectedItemShortDto.getAvailable(), actualItemShortDto.getAvailable());
        assertEquals(expectedItemShortDto.getRequestId(), actualItemShortDto.getRequestId());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateItem_shouldThrowNotFoundException_whenUserNotFound() {
        Long notFoundId = 1L;
        String name = "test item name";
        String description = "test item description";

        Boolean available = Boolean.TRUE;
        Long requestId = 2L;
        Long itemId = 10L;
        ItemShortDto itemToUpdateShortDto = ItemShortDto.builder()
                .name(name)
                .description(description)
                .available(available)
                .requestId(requestId)
                .build();
        when(userRepository.getUserById(notFoundId)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.update(notFoundId, itemId, itemToUpdateShortDto));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_shouldThrowNotFoundException_whenItemNotFound() {
        Long ownerId = 1L;
        String name = "test item name";
        String description = "test item description";
        Boolean available = Boolean.TRUE;
        Long requestId = 2L;
        Long itemId = 10L;
        ItemShortDto itemToUpdateShortDto = ItemShortDto.builder()
                .name(name)
                .description(description)
                .available(available)
                .requestId(requestId)
                .build();
        when(itemRepository.getItemById(itemId)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.update(ownerId, itemId, itemToUpdateShortDto));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_shouldThrowNotFoundException_whenItemOwnerNotFound() {
        Long userId = 1L;
        String userName = "test owner user";
        String userEmail = "user@test.com";
        User user = User.builder().id(userId).name(userName).email(userEmail).build();
        Long ownerId = 2L;
        String ownerName = "test owner user";
        String ownerEmail = "owner@test.com";
        User owner = User.builder().id(ownerId).name(ownerName).email(ownerEmail).build();
        when(userRepository.getUserById(ownerId)).thenReturn(owner);
        when(userRepository.getUserById(userId)).thenReturn(user);
        String name = "test item name";
        String description = "test item description";
        Boolean available = Boolean.TRUE;
        Long requestId = 2L;
        Long itemId = 10L;
        when(itemRepository.getItemById(itemId)).thenReturn(Item.builder()
                .id(itemId)
                .owner(owner)
                .name(name)
                .description(description)
                .available(available)
                .build());
        ItemShortDto itemToUpdateShortDto = ItemShortDto.builder()
                .name(name)
                .description(description)
                .available(available)
                .requestId(requestId)
                .build();

        assertThrows(NotFoundException.class, () -> itemService.update(userId, itemId, itemToUpdateShortDto));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void addComment_shouldThrowNotFoundException_whenUserNotFound() {
        Long notFoundId = 1L;
        Long itemId = 10L;
        String name = "test item name";
        String description = "test item description";
        Item item = Item.builder()
                .id(itemId)
                .name(name)
                .description(description)
                .build();
        when(itemRepository.getItemById(itemId)).thenReturn(item);
        when(userRepository.getUserById(notFoundId)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.addComment(notFoundId, itemId, new CommentShortDto("text")));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void addComment_shouldThrowNotFoundException_whenItemNotFound() {
        Long notFoundItemId = 10L;
        Long ownerId = 2L;
        String ownerName = "test owner user";
        String ownerEmail = "owner@test.com";
        User owner = User.builder().id(ownerId).name(ownerName).email(ownerEmail).build();
        when(userRepository.getUserById(ownerId)).thenReturn(owner);
        when(itemRepository.getItemById(notFoundItemId)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.addComment(ownerId, notFoundItemId, new CommentShortDto("text")));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void addComment_shouldThrowBadRequestException_whenNotExistValidBooking() {
        Long authorId = 1L;
        Long ownerId = 2L;
        String ownerName = "test owner user";
        String ownerEmail = "owner@test.com";
        User owner = User.builder().id(ownerId).name(ownerName).email(ownerEmail).build();
        when(userRepository.getUserById(ownerId)).thenReturn(owner);
        Long itemId = 10L;
        String name = "test item name";
        String description = "test item description";
        Item item = Item.builder()
                .id(itemId)
                .name(name)
                .description(description)
                .owner(owner)
                .build();
        when(itemRepository.getItemById(itemId)).thenReturn(item);
        when(bookingRepository.existValidBooking(authorId, itemId)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> itemService.addComment(authorId, itemId, new CommentShortDto("text")));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void addComment_shouldAddComment_whenExistValidBookingAndUserFoundAndItemFound() {
        Long authorId = 1L;
        String authorName = "test author user";
        String authorEmail = "author@test.com";
        User author = User.builder().id(authorId).name(authorName).email(authorEmail).build();
        when(userRepository.getUserById(authorId)).thenReturn(author);
        Long ownerId = 2L;
        String ownerName = "test owner user";
        String ownerEmail = "owner@test.com";
        User owner = User.builder().id(ownerId).name(ownerName).email(ownerEmail).build();
        when(userRepository.getUserById(ownerId)).thenReturn(owner);
        Long itemId = 10L;
        Long commentId = 1000L;
        LocalDateTime created = LocalDateTime.now();
        String name = "test item name";
        String description = "test item description";
        Item item = Item.builder()
                .id(itemId)
                .name(name)
                .description(description)
                .owner(owner)
                .build();
        String commentText = "text";
        when(itemRepository.getItemById(itemId)).thenReturn(item);
        when(bookingRepository.existValidBooking(authorId, itemId)).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(Comment.builder()
                .id(commentId)
                .item(item)
                .author(author)
                .created(created)
                .text(commentText)
                .build());
        CommentDto expectedCommentDto = CommentDto.builder()
                .id(commentId)
                .text(commentText)
                .authorName(authorName)
                .created(created)
                .build();

        CommentDto actualCommentDto = itemService.addComment(authorId, itemId, new CommentShortDto(commentText));

        assertNotNull(actualCommentDto);
        assertEquals(expectedCommentDto, actualCommentDto);
        assertEquals(expectedCommentDto.getId(), actualCommentDto.getId());
        assertEquals(expectedCommentDto.getAuthorName(), actualCommentDto.getAuthorName());
        assertEquals(expectedCommentDto.getCreated(), actualCommentDto.getCreated());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }
}
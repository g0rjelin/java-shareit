package ru.practicum.shareit.item.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
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

    User owner;
    User author;
    User requestor;
    Item item;
    List<Item> items;
    Booking prevBooking;
    Booking nextBooking;
    List<Booking> bookings;
    Comment comment;
    List<Comment> comments;
    ItemDto itemOwnerDto;
    ItemDto itemDto;
    List<ItemDto> itemDtos;
    ItemShortDto itemShortDto;
    ItemShortDto itemToSaveShortDto;
    ItemRequest itemRequest;

    @BeforeEach
    void bookingServiceTestSetUp() {
        Long ownerId = 1L;
        String ownerName = "test owner user";
        String ownerEmail = "owner@test.com";
        owner = User.builder().id(ownerId).name(ownerName).email(ownerEmail).build();
        Long authorId = 2L;
        String authorName = "test author user";
        String authorEmail = "author@test.com";
        author = User.builder().id(authorId).name(authorName).email(authorEmail).build();
        Long requestorId = 3L;
        String requestorName = "test requestor user";
        String requestorEmail = "requestor@test.com";
        requestor = User.builder().id(requestorId).name(requestorName).email(requestorEmail).build();
        Long itemId = 10L;
        String itemName = "test item name";
        String itemDescription = "test item description";
        Boolean available = true;
        Long requestId = 100L;
        String requestDescription = "test description";
        LocalDateTime created = LocalDateTime.now();
        itemRequest = ItemRequest.builder()
                .id(requestId)
                .description(requestDescription)
                .created(created)
                .requestor(requestor)
                .build();
        item = Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(available)
                .request(itemRequest)
                .owner(owner)
                .build();
        items = List.of(item);
        LocalDateTime prevBookingStartDate = LocalDateTime.of(2024, 1, 1, 12, 30);
        LocalDateTime prevBookingEndDate = LocalDateTime.of(2024, 1, 1, 18, 30);
        LocalDateTime nextBookingStartDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)).plusDays(1);
        LocalDateTime nextBookingEndDate = nextBookingStartDate.plusHours(10);
        Long prevBookingId = 1000L;
        Long nextBookingId = 2000L;
        prevBooking = Booking.builder()
                .id(prevBookingId)
                .status(BookingStatus.APPROVED)
                .start(prevBookingStartDate)
                .end(prevBookingEndDate)
                .item(item)
                .build();
        nextBooking = Booking.builder()
                .id(nextBookingId)
                .status(BookingStatus.APPROVED)
                .start(nextBookingStartDate)
                .end(nextBookingEndDate)
                .item(item)
                .build();
        bookings = List.of(prevBooking, nextBooking);
        Long commentId = 50L;
        String commentText = "test comment";
        comment = Comment.builder()
                .id(commentId)
                .text(commentText)
                .author(author)
                .item(item)
                .created(created)
                .build();
        comments = List.of(comment);
        CommentDto commentDto = CommentDto.builder()
                .id(commentId)
                .text(commentText)
                .authorName(authorName)
                .created(created)
                .build();
        itemDto = ItemDto.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(available)
                .request(itemRequest.getId())
                .comments(List.of(commentDto
                ))
                .build();
        itemOwnerDto = ItemDto.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .request(itemDto.getRequest())
                .lastBooking(prevBookingEndDate)
                .nextBooking(nextBookingStartDate)
                .comments(itemDto.getComments())
                .build();
        itemDtos = List.of(itemOwnerDto);
        itemShortDto = ItemShortDto.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .requestId(itemDto.getRequest())
                .build();
        itemToSaveShortDto = ItemShortDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest().getId())
                .build();
    }

    @Test
    void findAllItemsByOwnerId_shouldReturnCollectionItemDto_whenUserFound() {
        when(userRepository.getUserById(owner.getId())).thenReturn(owner);
        when(userRepository.getUserById(author.getId())).thenReturn(author);
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(items);
        when(bookingRepository.findByItem_Owner_Id(owner.getId())).thenReturn(bookings);
        when(commentRepository.findByItem_Owner_Id(owner.getId())).thenReturn(comments);
        List<ItemDto> expectedItemDtos = itemDtos;

        Collection<ItemDto> actualItemDtos = itemService.findAllItemsByOwnerId(owner.getId());

        assertEquals(expectedItemDtos.size(), actualItemDtos.size());
        assertIterableEquals(expectedItemDtos, actualItemDtos);
    }

    @Test
    void findAllItemsByOwnerId_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.getUserById(owner.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.findAllItemsByOwnerId(owner.getId()));
        verify(itemRepository, never()).findAllByOwnerId(anyLong());
        verify(commentRepository, never()).findByItem_Owner_Id(anyLong());
        verify(bookingRepository, never()).findByItem_Owner_Id(anyLong());
    }

    @Test
    void findItemById_shouldReturnItemDto_whenItemFound() {
        when(itemRepository.getItemById(item.getId())).thenReturn(item);
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(comments);
        ItemDto expectedItemDto = itemDto;

        ItemDto actualItemDto = itemService.findItemById(item.getId());

        assertNotNull(actualItemDto);
        assertEquals(expectedItemDto, actualItemDto);
        assertEquals(expectedItemDto.getId(), actualItemDto.getId());
        assertEquals(expectedItemDto.getName(), actualItemDto.getName());
        assertEquals(expectedItemDto.getDescription(), actualItemDto.getDescription());
        assertEquals(expectedItemDto.getAvailable(), actualItemDto.getAvailable());
        assertIterableEquals(expectedItemDto.getComments(), actualItemDto.getComments());
    }

    @Test
    void findItemById_shouldThrowNotFoundException_whenItemNotFound() {
        when(itemRepository.getItemById(item.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.findItemById(item.getId()));
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
        List<ItemShortDto> expectedItemShortDtos = List.of(itemShortDto);
        when(itemRepository.findAll(any(BooleanExpression.class))).thenReturn(List.of(item));

        Collection<ItemShortDto> actualItemDtos = itemService.searchItems(text);

        assertEquals(expectedItemShortDtos.size(), actualItemDtos.size());
        assertIterableEquals(expectedItemShortDtos, actualItemDtos);
        verify(itemRepository, times(1)).findAll(any(BooleanExpression.class));
    }

    @Test
    void createItem_shouldCreateItem_whenUserFoundAndNoRequestOrRequestExists() {
        when(userRepository.getUserById(owner.getId())).thenReturn(owner);
        when(itemRequestRepository.findById(requestor.getId())).thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        ItemShortDto expectedItemShortDto = itemShortDto;

        ItemShortDto actualItemShortDto = itemService.create(owner.getId(), itemToSaveShortDto);

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
        when(userRepository.getUserById(author.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.create(author.getId(), itemShortDto));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void createItem_shouldThrowNotFoundException_whenRequestNotFound() {
        when(itemRequestRepository.getItemRequestBy(itemRequest.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.create(owner.getId(), itemShortDto));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_shouldUpdateItemName_whenUserFoundAndItemFound() {
        String newItemName = "new item name";
        ItemShortDto itemToUpdateShortDto = ItemShortDto.builder()
                .name(newItemName)
                .build();
        when(userRepository.getUserById(owner.getId())).thenReturn(owner);
        when(itemRepository.getItemById(item.getId())).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        ItemShortDto expectedItemShortDto = ItemShortDto.builder()
                .id(item.getId())
                .name(newItemName)
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest().getId())
                .build();

        ItemShortDto actualItemShortDto = itemService.update(owner.getId(), item.getId(), itemToUpdateShortDto);

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
        String newItemDescription = "new item description";
        ItemShortDto itemToUpdateShortDto = ItemShortDto.builder()
                .description(newItemDescription)
                .build();
        when(userRepository.getUserById(owner.getId())).thenReturn(owner);
        when(itemRepository.getItemById(item.getId())).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        ItemShortDto expectedItemShortDto = ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(newItemDescription)
                .available(item.getAvailable())
                .requestId(item.getRequest().getId())
                .build();

        ItemShortDto actualItemShortDto = itemService.update(owner.getId(), item.getId(), itemToUpdateShortDto);

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
        Boolean newItemAvailable = !item.getAvailable();
        ItemShortDto itemToUpdateShortDto = ItemShortDto.builder()
                .available(!item.getAvailable())
                .build();
        when(userRepository.getUserById(owner.getId())).thenReturn(owner);
        when(itemRepository.getItemById(item.getId())).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        ItemShortDto expectedItemShortDto = ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(newItemAvailable)
                .requestId(item.getRequest().getId())
                .build();

        ItemShortDto actualItemShortDto = itemService.update(owner.getId(), item.getId(), itemToUpdateShortDto);

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
        when(userRepository.getUserById(owner.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.update(owner.getId(), item.getId(), itemToSaveShortDto));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_shouldThrowNotFoundException_whenItemNotFound() {
        when(itemRepository.getItemById(item.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.update(owner.getId(), item.getId(), itemToSaveShortDto));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_shouldThrowNotFoundException_whenItemOwnerNotFound() {
        when(userRepository.getUserById(owner.getId())).thenReturn(owner);
        when(userRepository.getUserById(author.getId())).thenReturn(author);
        when(itemRepository.getItemById(item.getId())).thenReturn(item);

        assertThrows(NotFoundException.class, () -> itemService.update(author.getId(), item.getId(), itemToSaveShortDto));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void addComment_shouldThrowNotFoundException_whenUserNotFound() {
        when(itemRepository.getItemById(item.getId())).thenReturn(item);
        when(userRepository.getUserById(author.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.addComment(author.getId(), item.getId(), new CommentShortDto("text")));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void addComment_shouldThrowNotFoundException_whenItemNotFound() {
        when(userRepository.getUserById(owner.getId())).thenReturn(owner);
        when(itemRepository.getItemById(item.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.addComment(owner.getId(), item.getId(), new CommentShortDto("text")));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void addComment_shouldThrowBadRequestException_whenNotExistValidBooking() {
        when(userRepository.getUserById(owner.getId())).thenReturn(owner);
        when(itemRepository.getItemById(item.getId())).thenReturn(item);
        when(bookingRepository.existValidBooking(author.getId(), item.getId())).thenReturn(false);

        assertThrows(BadRequestException.class, () -> itemService.addComment(author.getId(), item.getId(), new CommentShortDto("text")));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void addComment_shouldAddComment_whenExistValidBookingAndUserFoundAndItemFound() {
        when(userRepository.getUserById(author.getId())).thenReturn(author);
        when(userRepository.getUserById(owner.getId())).thenReturn(owner);
        Long commentId = 1000L;
        LocalDateTime created = LocalDateTime.now();
        String commentText = "text";
        when(itemRepository.getItemById(item.getId())).thenReturn(item);
        when(bookingRepository.existValidBooking(author.getId(), item.getId())).thenReturn(true);
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
                .authorName(author.getName())
                .created(created)
                .build();

        CommentDto actualCommentDto = itemService.addComment(author.getId(), item.getId(), new CommentShortDto(commentText));

        assertNotNull(actualCommentDto);
        assertEquals(expectedCommentDto, actualCommentDto);
        assertEquals(expectedCommentDto.getId(), actualCommentDto.getId());
        assertEquals(expectedCommentDto.getAuthorName(), actualCommentDto.getAuthorName());
        assertEquals(expectedCommentDto.getCreated(), actualCommentDto.getCreated());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }
}
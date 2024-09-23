package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotAllowedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    @Autowired
    private final BookingService bookingService;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRepository itemRepository;

    @Test
    void findBookingById_shouldReturnBookingDto_whenBookingFound() {
        Long bookerId = 1L;
        String bookerName = "test user user";
        String bookerEmail = "booker@test.com";
        User booker = User.builder().id(bookerId).name(bookerName).email(bookerEmail).build();
        when(userRepository.getUserById(bookerId)).thenReturn(booker);
        Long ownerId = 2L;
        String ownerName = "test user owner";
        String ownerEmail = "owner@test.com";
        User owner = User.builder().id(ownerId).name(ownerName).email(ownerEmail).build();
        when(userRepository.getUserById(ownerId)).thenReturn(owner);
        Long itemId = 10L;
        String itemName = "test item name";
        String itemDescription = "test item description";
        Boolean available = Boolean.TRUE;
        Item item = Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(available)
                .owner(owner)
                .build();
        Long bookingId = 1000L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(5);
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .item(item)
                .build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .booker(UserDto.builder().id(bookerId).name(bookerName).email(bookerEmail).build())
                .item(ItemShortDto.builder().id(itemId).name(itemName).description(itemDescription).available(available).build())
                .status(BookingStatus.APPROVED.toString())
                .build();

        BookingDto actualBookingDto = bookingService.findBookingById(bookerId, bookingId);

        assertNotNull(actualBookingDto);
        assertEquals(expectedBookingDto, actualBookingDto);
        assertEquals(expectedBookingDto.getId(), actualBookingDto.getId());
        assertEquals(expectedBookingDto.getStart(), actualBookingDto.getStart());
        assertEquals(expectedBookingDto.getEnd(), actualBookingDto.getEnd());
        assertEquals(expectedBookingDto.getBooker(), actualBookingDto.getBooker());
        assertEquals(expectedBookingDto.getItem(), actualBookingDto.getItem());
        assertEquals(expectedBookingDto.getStatus(), actualBookingDto.getStatus());
    }

    @Test
    void findBookingById_shouldThrowNotFoundException_whenBookingNotFound() {
        Long bookingId = 1000L;
        Long userId = 2L;
        String userName = "test user user";
        String userEmail = "user@test.com";
        User user = User.builder().id(userId).name(userName).email(userEmail).build();
        when(userRepository.getUserById(userId)).thenReturn(user);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.findBookingById(userId, bookingId));
    }

    @Test
    void findBookingById_shouldThrowNotFoundException_whenUserNotFound() {
        Long userId = 2L;
        Long bookingId = 1000L;
        Booking booking = Booking.builder()
                .id(bookingId)
                .item(Item.builder().id(10L).name("name").description("description").available(true).build())
                .build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.getUserById(userId)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> bookingService.findBookingById(userId, bookingId));
    }

    @Test
    void findBookingById_shouldThrowNotAllowException_whenCalledNotByBookerOrOwner() {
        Long bookerId = 1L;
        String bookerName = "test user user";
        String bookerEmail = "booker@test.com";
        User booker = User.builder().id(bookerId).name(bookerName).email(bookerEmail).build();
        when(userRepository.getUserById(bookerId)).thenReturn(booker);
        Long ownerId = 2L;
        String ownerName = "test user owner";
        String ownerEmail = "owner@test.com";
        User owner = User.builder().id(ownerId).name(ownerName).email(ownerEmail).build();
        when(userRepository.getUserById(ownerId)).thenReturn(owner);
        Long userId = 3L;
        String userName = "test user user";
        String userEmail = "user@test.com";
        User user = User.builder().id(userId).name(userName).email(userEmail).build();
        when(userRepository.getUserById(userId)).thenReturn(user);
        Long bookingId = 1000L;
        Booking booking = Booking.builder()
                .id(bookingId)
                .booker(booker)
                .item(Item.builder().id(10L).name("name").description("description").available(true).owner(owner).build())
                .status(BookingStatus.APPROVED)
                .build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NotAllowedException.class, () -> bookingService.findBookingById(userId, bookingId));
    }

    @Test
    void findBookingsByState_shouldReturnCollectionBookings() {
        Long bookerId = 1L;
        String bookerName = "test user user";
        String bookerEmail = "booker@test.com";
        User booker = User.builder().id(bookerId).name(bookerName).email(bookerEmail).build();
        when(userRepository.getUserById(bookerId)).thenReturn(booker);
        Long ownerId = 2L;
        String ownerName = "test user owner";
        String ownerEmail = "owner@test.com";
        User owner = User.builder().id(ownerId).name(ownerName).email(ownerEmail).build();
        when(userRepository.getUserById(ownerId)).thenReturn(owner);
        Long itemId = 10L;
        String itemName = "test item name";
        String itemDescription = "test item description";
        Boolean available = Boolean.TRUE;
        Item item = Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(available)
                .owner(owner)
                .build();
        Long bookingId = 1000L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(5);
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .item(item)
                .build();
        List<Booking> bookings = List.of(booking);
        Page<Booking> pagedBookings = new PageImpl<Booking>(bookings);
        when(bookingRepository.findAllByState(bookerId, BookingState.ALL.toString(), PageRequest.of(0, 10)))
                .thenReturn(pagedBookings);
        Collection<BookingDto> expectedBookingDtos = List.of(BookingDto.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .status(BookingStatus.APPROVED.toString())
                .item(ItemShortDto.builder()
                        .id(itemId)
                        .name(itemName)
                        .description(itemDescription)
                        .available(available)
                        .build())
                .booker(UserDto.builder().id(bookerId).name(bookerName).email(bookerEmail).build())
                .build());

        Collection<BookingDto> actualBookingDtos = bookingService.findBookingsByState(bookerId, BookingState.ALL, 0, 10);

        assertEquals(expectedBookingDtos.size(), actualBookingDtos.size());
        assertIterableEquals(expectedBookingDtos, actualBookingDtos);
    }

    @Test
    void findBookingsOwnerByState_shouldReturnCollectionBookings_whenOwnedItemsFound() {
        Long bookerId = 1L;
        String bookerName = "test user user";
        String bookerEmail = "booker@test.com";
        User booker = User.builder().id(bookerId).name(bookerName).email(bookerEmail).build();
        when(userRepository.getUserById(bookerId)).thenReturn(booker);
        Long ownerId = 2L;
        String ownerName = "test user owner";
        String ownerEmail = "owner@test.com";
        User owner = User.builder().id(ownerId).name(ownerName).email(ownerEmail).build();
        when(userRepository.getUserById(ownerId)).thenReturn(owner);
        Long itemId = 10L;
        String itemName = "test item name";
        String itemDescription = "test item description";
        Boolean available = Boolean.TRUE;
        Item item = Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(available)
                .owner(owner)
                .build();
        Long bookingId = 1000L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(5);
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .item(item)
                .build();
        List<Booking> bookings = List.of(booking);
        Page<Booking> pagedBookings = new PageImpl<Booking>(bookings);
        when(itemRepository.existsItemsByOwnerId(ownerId)).thenReturn(true);
        when(bookingRepository.findAllOwnerByState(ownerId, BookingState.ALL.toString(), PageRequest.of(0, 10)))
                .thenReturn(pagedBookings);
        Collection<BookingDto> expectedBookingDtos = List.of(BookingDto.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .status(BookingStatus.APPROVED.toString())
                .item(ItemShortDto.builder()
                        .id(itemId)
                        .name(itemName)
                        .description(itemDescription)
                        .available(available)
                        .build())
                .booker(UserDto.builder().id(bookerId).name(bookerName).email(bookerEmail).build())
                .build());

        Collection<BookingDto> actualBookingDtos = bookingService.findBookingsOwnerByState(ownerId, BookingState.ALL, 0, 10);

        assertEquals(expectedBookingDtos.size(), actualBookingDtos.size());
        assertIterableEquals(expectedBookingDtos, actualBookingDtos);
    }

    @Test
    void findBookingsOwnerByState_shouldThrowNotFoundException_whenOwnedItemsNotFound() {
        Long ownerId = 1L;
        when(itemRepository.existsItemsByOwnerId(ownerId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingService.findBookingsOwnerByState(ownerId, BookingState.ALL, 0, 10));
        verify(itemRepository, never()).findAllByOwnerId(anyLong());
    }

    @Test
    void createBooking_shouldReturnBookingDto_whenAllConditionsMet() {
        Long bookerId = 1L;
        String bookerName = "test user user";
        String bookerEmail = "booker@test.com";
        User booker = User.builder().id(bookerId).name(bookerName).email(bookerEmail).build();
        when(userRepository.getUserById(bookerId)).thenReturn(booker);
        Long itemId = 10L;
        String itemName = "test item name";
        String itemDescription = "test item description";
        Boolean available = true;
        Long ownerId = 2L;
        User owner = User.builder().id(ownerId).name("owner").email("owner@test.com").build();
        Item item = Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(available)
                .owner(owner)
                .build();
        when(itemRepository.getItemById(itemId)).thenReturn(item);
        Long bookingId = 1000L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        when(bookingRepository.existIntersectingBookingDatesForItem(start, end, item.getId()))
                .thenReturn(false);
        BookingShortDto bookingShortDto = BookingShortDto.builder()
                .start(start)
                .end(end)
                .itemId(itemId)
                .build();
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .booker(UserDto.builder().id(bookerId).name(bookerName).email(bookerEmail).build())
                .status(BookingStatus.APPROVED.toString())
                .item(ItemShortDto.builder().id(itemId).name(itemName).description(itemDescription).available(available).build())
                .build();

        BookingDto actualBookingDto = bookingService.create(bookerId, bookingShortDto);

        assertNotNull(actualBookingDto);
        assertEquals(expectedBookingDto, actualBookingDto);
        assertEquals(expectedBookingDto.getItem().getId(), actualBookingDto.getItem().getId());
        assertEquals(expectedBookingDto.getItem().getName(), actualBookingDto.getItem().getName());
        assertEquals(expectedBookingDto.getItem().getDescription(), actualBookingDto.getItem().getDescription());
        assertEquals(expectedBookingDto.getItem().getAvailable(), actualBookingDto.getItem().getAvailable());
        assertEquals(expectedBookingDto.getStart(), actualBookingDto.getStart());
        assertEquals(expectedBookingDto.getEnd(), actualBookingDto.getEnd());
        assertEquals(expectedBookingDto.getStatus(), actualBookingDto.getStatus());
        assertEquals(expectedBookingDto.getBooker().getId(), actualBookingDto.getBooker().getId());
        assertEquals(expectedBookingDto.getBooker().getName(), actualBookingDto.getBooker().getName());
        assertEquals(expectedBookingDto.getBooker().getEmail(), actualBookingDto.getBooker().getEmail());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_shouldThrowNotFoundException_whenUserNotFound() {
        Long bookerId = 1L;
        when(userRepository.getUserById(bookerId)).thenThrow(NotFoundException.class);
        Long itemId = 10L;
        String itemName = "test item name";
        String itemDescription = "test item description";
        Boolean available = false;
        Long ownerId = 2L;
        User owner = User.builder().id(ownerId).name("owner").email("owner@test.com").build();
        Item item = Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(available)
                .owner(owner)
                .build();
        when(itemRepository.getItemById(itemId)).thenReturn(item);

        assertThrows(NotFoundException.class, () -> bookingService.create(bookerId, BookingShortDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build()));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_shouldThrowNotFoundException_whenItemNotFound() {
        Long bookerId = 1L;
        String bookerName = "test user user";
        String bookerEmail = "booker@test.com";
        User booker = User.builder().id(bookerId).name(bookerName).email(bookerEmail).build();
        when(userRepository.getUserById(bookerId)).thenReturn(booker);
        Long itemId = 10L;
        when(itemRepository.getItemById(itemId)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> bookingService.create(bookerId, BookingShortDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build()));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_shouldThrowBadRequestException_whenItemNotAvailable() {
        Long bookerId = 1L;
        String bookerName = "test user user";
        String bookerEmail = "booker@test.com";
        User booker = User.builder().id(bookerId).name(bookerName).email(bookerEmail).build();
        when(userRepository.getUserById(bookerId)).thenReturn(booker);
        Long itemId = 10L;
        String itemName = "test item name";
        String itemDescription = "test item description";
        Boolean available = false;
        Long ownerId = 2L;
        User owner = User.builder().id(ownerId).name("owner").email("owner@test.com").build();
        Item item = Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(available)
                .owner(owner)
                .build();
        when(itemRepository.getItemById(itemId)).thenReturn(item);

        assertThrows(BadRequestException.class, () -> bookingService.create(bookerId, BookingShortDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build()));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_shouldThrowBadRequestException_whenItemOwnerCantBook() {
        Long bookerId = 1L;
        String bookerName = "test user user";
        String bookerEmail = "booker@test.com";
        User booker = User.builder().id(bookerId).name(bookerName).email(bookerEmail).build();
        when(userRepository.getUserById(bookerId)).thenReturn(booker);
        Long itemId = 10L;
        String itemName = "test item name";
        String itemDescription = "test item description";
        Boolean available = true;
        Item item = Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(available)
                .owner(booker)
                .build();
        when(itemRepository.getItemById(itemId)).thenReturn(item);

        assertThrows(BadRequestException.class, () -> bookingService.create(bookerId, BookingShortDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build()));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_shouldThrowBadRequestException_whenBookingHasIntersections() {
        Long bookerId = 1L;
        String bookerName = "test user user";
        String bookerEmail = "booker@test.com";
        User booker = User.builder().id(bookerId).name(bookerName).email(bookerEmail).build();
        when(userRepository.getUserById(bookerId)).thenReturn(booker);
        Long itemId = 10L;
        String itemName = "test item name";
        String itemDescription = "test item description";
        Boolean available = true;
        Long ownerId = 2L;
        User owner = User.builder().id(ownerId).name("owner").email("owner@test.com").build();
        Item item = Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(available)
                .owner(owner)
                .build();
        when(itemRepository.getItemById(itemId)).thenReturn(item);
        Long bookingId = 1000L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        when(bookingRepository.existIntersectingBookingDatesForItem(start, end, item.getId()))
                        .thenReturn(true);

        assertThrows(BadRequestException.class, () -> bookingService.create(bookerId, BookingShortDto.builder()
                .itemId(itemId)
                .start(start)
                .end(end)
                .build()));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateBooking_shouldReturnBookingDto_whenAllConditionsMet() {
        Long userId = 1L;
        String userName = "test user user";
        String userEmail = "test@test.com";
        User user = User.builder().id(userId).name(userName).email(userEmail).build();
        Long ownerId = 2L;
        User owner = User.builder().id(ownerId).name("owner").email("owner@test.com").build();
        Long itemId = 10L;
        String itemName = "test item name";
        String itemDescription = "test item description";
        Boolean available = false;
        Item item = Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(available)
                .owner(owner)
                .build();
        Long bookingId = 1000L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(5);
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .booker(user)
                .status(BookingStatus.WAITING)
                .item(item)
                .build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        Booking returnedBooking = Booking.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .item(item)
                .build();
        when(bookingRepository.save(booking)).thenReturn(returnedBooking);
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .item(ItemShortDto.builder()
                        .id(itemId)
                        .name(itemName)
                        .description(itemDescription)
                        .available(available)
                        .build())
                .status(BookingStatus.APPROVED.toString())
                .booker(UserDto.builder().id(userId).email(userEmail).name(userName).build())
                .build();

        BookingDto actualBookingDto = bookingService.update(ownerId, bookingId, true);

        assertNotNull(actualBookingDto);
        assertEquals(expectedBookingDto, actualBookingDto);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void updateBooking_shouldThrowNotFoundException_whenBookingNotFound() {
        Long bookingId = 1L;
        Long ownerId = 2L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,  () -> bookingService.update(ownerId, bookingId, true));
        verify(bookingRepository, never()).save(any(Booking.class));
    }


        @Test
    void updateBooking_shouldThrowBadRequest_whenNotItemOwner() {
        Long userId = 1L;
        String userName = "test user user";
        String userEmail = "test@test.com";
        User user = User.builder().id(userId).name(userName).email(userEmail).build();
        Long ownerId = 2L;
        User owner = User.builder().id(ownerId).name("owner").email("owner@test.com").build();
        Long itemId = 10L;
        String itemName = "test item name";
        String itemDescription = "test item description";
        Boolean available = false;
        Item item = Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(available)
                .owner(user)
                .build();
        Long bookingId = 1000L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(5);
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .booker(user)
                .status(BookingStatus.WAITING)
                .item(item)
                .build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));


        assertThrows(NotAllowedException.class, () -> bookingService.update(ownerId, bookingId, true));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateBooking_shouldNotAllowedException_whenStatusNotWaiting() {
        Long userId = 1L;
        String userName = "test user user";
        String userEmail = "test@test.com";
        User user = User.builder().id(userId).name(userName).email(userEmail).build();
        Long ownerId = 2L;
        User owner = User.builder().id(ownerId).name("owner").email("owner@test.com").build();
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        Long itemId = 10L;
        String itemName = "test item name";
        String itemDescription = "test item description";
        Boolean available = false;
        Item item = Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(available)
                .owner(owner)
                .build();
        Long bookingId = 1000L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(5);
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .item(item)
                .build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NotAllowedException.class, () -> bookingService.update(ownerId, bookingId, true));
        verify(bookingRepository, never()).save(any(Booking.class));
    }
}

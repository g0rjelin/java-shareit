package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
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

    User user;
    User booker;
    User owner;
    Item item;
    Booking booking;
    BookingDto bookingDto;
    Collection<BookingDto> bookingDtos;
    List<Booking> bookings;
    BookingShortDto bookingToCreateShortDto;

    @BeforeEach
    void bookingServiceTestSetUp() {
        Long userId = 1L;
        String userName = "test user user";
        String userEmail = "user@test.com";
        user = User.builder().id(userId).name(userName).email(userEmail).build();
        Long bookerId = 2L;
        String bookerName = "test user user";
        String bookerEmail = "booker@test.com";
        booker = User.builder().id(bookerId).name(bookerName).email(bookerEmail).build();
        Long ownerId = 3L;
        String ownerName = "test user owner";
        String ownerEmail = "owner@test.com";
        owner = User.builder().id(ownerId).name(ownerName).email(ownerEmail).build();
        Long itemId = 10L;
        String itemName = "test item name";
        String itemDescription = "test item description";
        Boolean available = Boolean.TRUE;
        item = Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(available)
                .owner(owner)
                .build();
        Long bookingId = 1000L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(5);
        booking = Booking.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .item(item)
                .build();
        bookings = List.of(booking);
        bookingDto = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus().toString())
                .item(ItemShortDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .build()
                )
                .booker(UserDto.builder().id(booker.getId()).name(booker.getName()).email(booker.getEmail()).build())
                .build();
        bookingDtos = List.of(bookingDto);
        bookingToCreateShortDto = BookingShortDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(item.getId())
                .build();
    }

    @Test
    void findBookingById_shouldReturnBookingDto_whenBookingFound() {
        when(userRepository.getUserById(booker.getId())).thenReturn(booker);
        when(userRepository.getUserById(owner.getId())).thenReturn(owner);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        BookingDto expectedBookingDto = bookingDto;

        BookingDto actualBookingDto = bookingService.findBookingById(booker.getId(), booking.getId());

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
        when(userRepository.getUserById(user.getId())).thenReturn(user);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.findBookingById(user.getId(), booking.getId()));
    }

    @Test
    void findBookingById_shouldThrowNotFoundException_whenUserNotFound() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.getUserById(user.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> bookingService.findBookingById(user.getId(), booking.getId()));
    }

    @Test
    void findBookingById_shouldThrowNotAllowException_whenCalledNotByBookerOrOwner() {
        when(userRepository.getUserById(booker.getId())).thenReturn(booker);
        when(userRepository.getUserById(owner.getId())).thenReturn(owner);
        when(userRepository.getUserById(user.getId())).thenReturn(user);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(NotAllowedException.class, () -> bookingService.findBookingById(user.getId(), booking.getId()));
    }

    @Test
    void findBookingsByState_shouldReturnCollectionBookings() {
        when(userRepository.getUserById(booker.getId())).thenReturn(booker);
        when(userRepository.getUserById(owner.getId())).thenReturn(owner);
        Page<Booking> pagedBookings = new PageImpl<Booking>(bookings);
        when(bookingRepository.findAllByState(booker.getId(), BookingState.ALL.toString(), PageRequest.of(0, 10)))
                .thenReturn(pagedBookings);
        Collection<BookingDto> expectedBookingDtos = bookingDtos;

        Collection<BookingDto> actualBookingDtos = bookingService.findBookingsByState(booker.getId(), BookingState.ALL, 0, 10);

        assertEquals(expectedBookingDtos.size(), actualBookingDtos.size());
        assertIterableEquals(expectedBookingDtos, actualBookingDtos);
    }

    @Test
    void findBookingsOwnerByState_shouldReturnCollectionBookings_whenOwnedItemsFound() {
        when(userRepository.getUserById(booker.getId())).thenReturn(booker);
        when(userRepository.getUserById(owner.getId())).thenReturn(owner);
        Page<Booking> pagedBookings = new PageImpl<Booking>(bookings);
        when(itemRepository.existsItemsByOwnerId(owner.getId())).thenReturn(true);
        when(bookingRepository.findAllOwnerByState(owner.getId(), BookingState.ALL.toString(), PageRequest.of(0, 10)))
                .thenReturn(pagedBookings);
        Collection<BookingDto> expectedBookingDtos = bookingDtos;

        Collection<BookingDto> actualBookingDtos = bookingService.findBookingsOwnerByState(owner.getId(), BookingState.ALL, 0, 10);

        assertEquals(expectedBookingDtos.size(), actualBookingDtos.size());
        assertIterableEquals(expectedBookingDtos, actualBookingDtos);
    }

    @Test
    void findBookingsOwnerByState_shouldThrowNotFoundException_whenOwnedItemsNotFound() {
        when(itemRepository.existsItemsByOwnerId(owner.getId())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingService.findBookingsOwnerByState(owner.getId(), BookingState.ALL, 0, 10));
        verify(itemRepository, never()).findAllByOwnerId(anyLong());
    }

    @Test
    void createBooking_shouldReturnBookingDto_whenAllConditionsMet() {
        when(userRepository.getUserById(booker.getId())).thenReturn(booker);
        when(itemRepository.getItemById(item.getId())).thenReturn(item);
        when(bookingRepository.existIntersectingBookingDatesForItem(booking.getStart(), booking.getEnd(), item.getId()))
                .thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDto expectedBookingDto = bookingDto;

        BookingDto actualBookingDto = bookingService.create(booker.getId(), bookingToCreateShortDto);

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
        when(userRepository.getUserById(booker.getId())).thenThrow(NotFoundException.class);
        when(itemRepository.getItemById(item.getId())).thenReturn(item);

        assertThrows(NotFoundException.class, () -> bookingService.create(booker.getId(), bookingToCreateShortDto));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_shouldThrowNotFoundException_whenItemNotFound() {
        when(userRepository.getUserById(booker.getId())).thenReturn(booker);
        when(itemRepository.getItemById(item.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> bookingService.create(booker.getId(), bookingToCreateShortDto));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_shouldThrowBadRequestException_whenItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.getUserById(booker.getId())).thenReturn(booker);
        when(itemRepository.getItemById(item.getId())).thenReturn(item);

        assertThrows(BadRequestException.class, () -> bookingService.create(booker.getId(), bookingToCreateShortDto));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_shouldThrowBadRequestException_whenItemOwnerCantBook() {
        item.setOwner(booker);
        when(userRepository.getUserById(booker.getId())).thenReturn(booker);
        when(itemRepository.getItemById(item.getId())).thenReturn(item);

        assertThrows(BadRequestException.class, () -> bookingService.create(booker.getId(), bookingToCreateShortDto));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_shouldThrowBadRequestException_whenBookingHasIntersections() {
        when(userRepository.getUserById(booker.getId())).thenReturn(booker);
        when(itemRepository.getItemById(item.getId())).thenReturn(item);
        when(bookingRepository.existIntersectingBookingDatesForItem(booking.getStart(), booking.getEnd(), item.getId()))
                .thenReturn(true);

        assertThrows(BadRequestException.class, () -> bookingService.create(booker.getId(), bookingToCreateShortDto));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateBooking_shouldReturnBookingDto_whenAllConditionsMet() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        Booking returnedBooking = Booking.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .item(item)
                .build();
        when(bookingRepository.save(booking)).thenReturn(returnedBooking);
        BookingDto expectedBookingDto = bookingDto;

        BookingDto actualBookingDto = bookingService.update(owner.getId(), booking.getId(), true);

        assertNotNull(actualBookingDto);
        assertEquals(expectedBookingDto, actualBookingDto);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void updateBooking_shouldThrowNotFoundException_whenBookingNotFound() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.update(owner.getId(), booking.getId(), true));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateBooking_shouldThrowBadRequest_whenNotItemOwner() {
        item.setOwner(user);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        assertThrows(NotAllowedException.class, () -> bookingService.update(owner.getId(), booking.getId(), true));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateBooking_shouldNotAllowedException_whenStatusNotWaiting() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(NotAllowedException.class, () -> bookingService.update(owner.getId(), booking.getId(), true));
        verify(bookingRepository, never()).save(any(Booking.class));
    }
}

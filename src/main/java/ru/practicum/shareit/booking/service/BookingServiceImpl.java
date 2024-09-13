package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotAllowedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.booking.validation.BookingValidation;

import java.util.Collection;
import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    final UserRepository userRepository;
    final ItemRepository itemRepository;
    final BookingRepository bookingRepository;

    static final String USER_NOT_FOUND_MSG = "Пользователь с id = %d не найден";
    static final String ITEM_NOT_FOUND_MSG = "Вещь с id = %d не найдена";
    static final String ITEM_NOT_AVAILABLE_MSG = "Вещь с id = %d недоступна для бронирования";
    static final String BOOKING_NOT_FOUND_MSG = "Бронирование с id = %d не найдено";
    static final String NOT_ITEM_OWNER_MSG = "Вещь не принадлежит пользователю с id = %d";
    static final String NOT_BOOKER_NOR_OWNER_MSG = "Пользователь с id = %d не является ни владельцем вещи, ни бронирующим";
    static final String OWNED_ITEMS_NOT_FOUND_MSG = "У пользователя с id = %d не найдены принадлежащие ему вещи";
    static final String OWNER_CANT_BOOK_MSG = "Пользователь не может забронировать принадлежащую ему вещь с id = %d";
    static final String BOOKING_HAS_INTERSECTIONS_MSG = "Нельзя забронировать вещь, так как запрошенные даты бронирования " +
            " пересекаются с существующими интервалами бронирования";
    static final String STATUS_NOT_WAITING_MSG = "Для подтверждения у бронирования c id = %d должен быть статус WAITING";

    @Override
    public BookingDto findBookingById(Long userId, Long bookingId) {
        User user = getUserById(userId);
        Booking booking = getBookingById(bookingId);
        if (!user.equals(booking.getBooker()) && !user.equals(booking.getItem().getOwner())) {
            throw new NotAllowedException(String.format(NOT_BOOKER_NOR_OWNER_MSG, userId));
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> findBookingsByState(Long bookerId, BookingState state) {
        return BookingMapper.toBookingDto(bookingRepository.findAllByState(bookerId, state.toString()));
    }

    @Override
    public Collection<BookingDto> findBookingsOwnerByState(Long ownerId, BookingState state) {
        if (!itemRepository.existsItemsByOwnerId(ownerId)) {
            throw new NotFoundException(String.format(OWNED_ITEMS_NOT_FOUND_MSG, ownerId));
        }
        return BookingMapper.toBookingDto(bookingRepository.findAllOwnerByState(ownerId, state.toString()));
    }

    @Override
    public BookingDto create(Long bookerId, BookingShortDto newBookingShortDto) {
        User booker = getUserById(bookerId);
        Item item = getItemById(newBookingShortDto.getItemId());
        BookingValidation.validateBookingDate(newBookingShortDto);
        if (!item.getAvailable()) {
            throw new BadRequestException(String.format(ITEM_NOT_AVAILABLE_MSG, item.getId()));
        }
        if (item.getOwner().equals(booker)) {
            throw new BadRequestException(String.format(OWNER_CANT_BOOK_MSG, item.getId()));
        }
        if (bookingRepository.existIntersectingBookingDatesForItem(newBookingShortDto.getStart(), newBookingShortDto.getEnd(), item.getId())) {
            throw new BadRequestException(BOOKING_HAS_INTERSECTIONS_MSG);
        }
        Booking newBooking = BookingMapper.toNewBooking(newBookingShortDto, booker, item);
        return BookingMapper.toBookingDto(bookingRepository.save(newBooking));
    }

    @Override
    public BookingDto update(Long ownerId, Long bookingId, Boolean approved) {
        Optional<User> optOwner = userRepository.findById(ownerId);
        Booking booking = getBookingById(bookingId);
        if (optOwner.isEmpty() || !optOwner.get().equals(booking.getItem().getOwner())) {
            throw new NotAllowedException(String.format(NOT_ITEM_OWNER_MSG, ownerId));
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new NotAllowedException(String.format(STATUS_NOT_WAITING_MSG, bookingId));
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND_MSG, userId)));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format(ITEM_NOT_FOUND_MSG, itemId)));
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(BOOKING_NOT_FOUND_MSG, bookingId)));
    }
}

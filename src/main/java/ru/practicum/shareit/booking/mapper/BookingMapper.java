package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemDto(booking.getItem()))
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .status(booking.getStatus().toString())
                .build();
    }

    public static List<BookingDto> toBookingDto(Iterable<Booking> bookings) {
        List<BookingDto> bookingDtos = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtos.add(toBookingDto(booking));
        }
        return bookingDtos;
    }

    public static Booking toNewBooking(BookingShortDto bookingShortDto, User booker, Item item) {
        return Booking.builder()
                .start(bookingShortDto.getStart())
                .end(bookingShortDto.getEnd())
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
    }

}

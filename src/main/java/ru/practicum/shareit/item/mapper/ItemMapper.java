package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ItemMapper {
    public static ItemShortDto toItemDto(Item item) {
        return ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static List<ItemShortDto> toItemDto(Iterable<Item> items) {
        List<ItemShortDto> itemShortDtos = new ArrayList<>();
        for (Item item : items) {
            itemShortDtos.add(toItemDto(item));
        }
        return itemShortDtos;
    }

    public static ItemDto toItemDto(Item item, Collection<Comment> comments) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest().getId() : null)
                .comments(CommentMapper.toCommentDto(comments))
                .build();
    }

    public static List<ItemDto> toItemOwnerDto(Iterable<Item> items, Collection<Comment> comments, Collection<Booking> bookings) {
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            Optional<LocalDateTime> lastBooking = bookings.stream()
                    .filter(booking -> booking.getItem().getId().equals(item.getId()))
                    .map(Booking::getEnd)
                    .filter(end -> end.isBefore(LocalDateTime.now()))
                    .max(Comparator.naturalOrder());
            Optional<LocalDateTime> nextBooking = bookings.stream()
                    .filter(booking -> booking.getItem().getId().equals(item.getId()))
                    .map(Booking::getStart)
                    .filter(start -> start.isAfter(LocalDateTime.now()))
                    .min(Comparator.naturalOrder());
            Collection<Comment> itemComments = comments.stream()
                    .filter(comment -> comment.getItem().getId().equals(item.getId()))
                    .toList();
            itemDtos.add(ItemDto.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .description(item.getDescription())
                    .available(item.getAvailable())
                    .request(item.getRequest() != null ? item.getRequest().getId() : null)
                    .lastBooking(lastBooking.orElse(null))
                    .nextBooking(nextBooking.orElse(null))
                    .comments(CommentMapper.toCommentDto(itemComments))
                    .build());
        }
        return itemDtos;
    }

    public static Item toItem(ItemShortDto itemShortDto, User owner) {
        return Item.builder()
                .name(itemShortDto.getName())
                .description(itemShortDto.getDescription())
                .available(itemShortDto.getAvailable())
                .owner(owner)
                .build();
    }
}

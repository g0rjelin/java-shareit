package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Collection;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class ItemDto {
    final Long id;
    final String name;
    final String description;
    final Boolean available;
    final Long request;
    final LocalDateTime lastBooking;
    final LocalDateTime nextBooking;
    Collection<CommentDto> comments;
}

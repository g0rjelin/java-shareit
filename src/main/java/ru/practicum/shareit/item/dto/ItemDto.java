package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    final Long id;
    final String name;
    final String description;
    final Boolean available;
    final Long request;
    final LocalDateTime lastBooking;
    final LocalDateTime nextBooking;
    Collection<CommentDto> comments;
}

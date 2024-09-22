package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class ItemShortDto {
    final Long id;

    final String name;

    final String description;

    final Boolean available;

    final Long requestId;
}

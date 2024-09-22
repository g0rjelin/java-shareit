package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;

import java.time.LocalDateTime;
import java.util.Collection;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class ItemRequestFullDto {
    final Long id;
    final String description;
    final LocalDateTime created;
    Collection<ItemForItemRequestDto> items;
}

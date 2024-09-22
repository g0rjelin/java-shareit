package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class ItemRequestDto {
    final Long id;
    final String description;
    final LocalDateTime created;
}

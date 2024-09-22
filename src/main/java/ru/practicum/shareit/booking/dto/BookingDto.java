package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class BookingDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    final Long id;
    final LocalDateTime start;
    final LocalDateTime end;
    final ItemShortDto item;
    final UserDto booker;
    final String status;
}
